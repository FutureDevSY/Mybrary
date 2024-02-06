package com.mybrary.backend.domain.member.service.impl;

import com.mybrary.backend.domain.follow.entity.Follow;
import com.mybrary.backend.domain.follow.repository.FollowRepository;
import com.mybrary.backend.domain.member.dto.FollowerDto;
import com.mybrary.backend.domain.member.dto.FollowingDto;
import com.mybrary.backend.domain.member.dto.LoginRequestDto;
import com.mybrary.backend.domain.member.dto.MemberUpdateDto;
import com.mybrary.backend.domain.member.dto.MyFollowerDto;
import com.mybrary.backend.domain.member.dto.MyFollowingDto;
import com.mybrary.backend.domain.member.dto.PasswordUpdateDto;
import com.mybrary.backend.domain.member.dto.SecessionRequestDto;
import com.mybrary.backend.domain.member.dto.SignupRequestDto;
import com.mybrary.backend.domain.member.dto.login.LoginResponseDto;
import com.mybrary.backend.domain.member.dto.login.MemberInfo;
import com.mybrary.backend.domain.member.entity.Member;
import com.mybrary.backend.domain.member.repository.MemberRepository;
import com.mybrary.backend.domain.member.service.MemberService;
import com.mybrary.backend.global.exception.member.DuplicateEmailException;
import com.mybrary.backend.global.exception.member.EmailNotFoundException;
import com.mybrary.backend.global.exception.member.InvalidLoginAttemptException;
import com.mybrary.backend.global.exception.member.PasswordMismatchException;
import com.mybrary.backend.global.jwt.TokenInfo;
import com.mybrary.backend.global.jwt.provider.TokenProvider;
import com.mybrary.backend.global.jwt.repository.RefreshTokenRepository;
import com.mybrary.backend.global.jwt.service.TokenService;
import com.mybrary.backend.global.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final TokenProvider tokenProvider;
    private final CookieUtil cookieUtil;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    @Override
    public Long create(SignupRequestDto requestDto) {

        /* 비밀번호 불일치 */
        checkPasswordConfirmation(requestDto.getPassword(), requestDto.getPasswordConfirm());

        /* 이메일 중복 검증 */
        memberRepository.findByEmail(requestDto.getEmail())
                        .ifPresent(this::throwDuplicateEmailException);

        Member member = Member.of(requestDto, passwordEncoder.encode(requestDto.getPassword()));
        memberRepository.save(member);
        return member.getId();
    }

    @Transactional
    @Override
    public LoginResponseDto login(LoginRequestDto requestDto, HttpServletResponse response) {
        log.info("event=LoginAttempt, email={}", requestDto.getEmail());

        Member member = findMemberByEmail(requestDto.getEmail());
        isPasswordMatchingWithEncoded(requestDto.getPassword(), member.getPassword());
        removeOldRefreshToken(requestDto, member);

        TokenInfo tokenInfo = tokenProvider.generateTokenInfo(member.getEmail());
        tokenService.saveToken(tokenInfo);
        cookieUtil.addCookie("RefreshToken", tokenInfo.getRefreshToken(), tokenProvider.getREFRESH_TOKEN_TIME(), response);

        return LoginResponseDto.builder()
                               .token(tokenInfo.getAccessToken())
                               .memberInfo(MemberInfo.builder()
                                                     .memberId(member.getId())
                                                     .email(member.getEmail())
                                                     .nickname(member.getNickname())
                                                     .profileImageUrl(member.getProfileImage() == null ? ""
                                                                          : member.getProfileImage().getUrl())
                                                     .build())
                               .build();
    }

    @Override
    public Member findMember(String email) {
        return memberRepository.findByEmail(email).orElseThrow(EmailNotFoundException::new);
    }


    @Override
    public List<MyFollowingDto> getAllMyFollowing(Long myId) {
        List<Member> myFollowing = memberRepository.getAllFollowing(myId);

        List<MyFollowingDto> list = new ArrayList<>();
        for (Member member : myFollowing) {
            list.add(new MyFollowingDto(member.getId(), member.getName(), member.getNickname(), null));
        }

        return list;
    }

    @Override
    public List<MyFollowerDto> getAllMyFollower(Long myId) {

        List<Member> myFollower = memberRepository.getAllFollower(myId);

        List<MyFollowerDto> list = new ArrayList<>();
        for (Member member : myFollower) {
            Follow follow = memberRepository.isFollowed(myId, member.getId());
            boolean isFollowed = false;
            if (follow != null) {
                isFollowed = true;
            }
            list.add(new MyFollowerDto(member.getId(), member.getName(), member.getNickname(), null, isFollowed));
        }

        return list;
    }

    @Override
    public List<FollowingDto> getAllFollowing(Long myId, Long memberId) {

        List<Member> Following = memberRepository.getAllFollowing(memberId);

        List<FollowingDto> list = new ArrayList<>();
        for (Member member : Following) {
            Follow follow = memberRepository.isFollowed(myId, member.getId());
            boolean isFollowed = false;
            if (follow != null) {
                isFollowed = true;
            }
            list.add(new FollowingDto(member.getId(), member.getName(), member.getNickname(), null, isFollowed));
        }

        return list;

    }

    @Override
    public List<FollowerDto> getAllFollower(Long myId, Long memberId) {
        List<Member> myFollower = memberRepository.getAllFollower(memberId);

        List<FollowerDto> list = new ArrayList<>();
        for (Member member : myFollower) {
            Follow follow = memberRepository.isFollowed(myId, member.getId());
            boolean isFollowed = false;
            if (follow != null) {
                isFollowed = true;
            }
            list.add(new FollowerDto(member.getId(), member.getName(), member.getNickname(), null, isFollowed));
        }

        return list;
    }

    @Transactional
    @Override
    public void follow(Long myId, Long memberId) {
        Member me = memberRepository.findById(myId).get();
        Member you = memberRepository.findById(memberId).get();
        Follow follow = Follow.builder()
                              .following(you)
                              .follower(me)
                              .build();
        followRepository.save(follow);
    }

    @Transactional
    @Override
    public void unfollow(Long myId, Long memberId) {
        Follow follow = followRepository.findFollow(myId, memberId);
        followRepository.delete(follow);
    }

    @Transactional
    @Override
    public void deleteFollower(Long myId, Long memberId) {
        Follow follow = followRepository.findFollow(memberId, myId);
        followRepository.delete(follow);
    }

    @Transactional
    @Override
    public void updateProfile(MemberUpdateDto member) {
        Member me = memberRepository.findById(member.getMemberId()).get();
        me.updateNickname(member.getNickname());
        me.updateIntro(member.getIntro());
        me.updateIsProfilePublic(member.isProfilePublic());
        me.updateIsNotifyEnable(member.isNotifyEnable());
        /* 프로필이미지 처리 작성해야함 */
    }

    @Transactional
    @Override
    public void updatePassword(Long myId, PasswordUpdateDto password) {

        /* 비밀번호 불일치 */
        if (!password.getPassword().equals(password.getPasswordConfirm())) {
            throw new PasswordMismatchException();
        }

        Member me = memberRepository.findById(myId).get();
        me.updatePassword(passwordEncoder.encode(password.getPassword()));

    }

    @Transactional
    @Override
    public void secession(SecessionRequestDto secession) {

        Member member = memberRepository.findByEmail(secession.getEmail())
                                        .orElseThrow(InvalidLoginAttemptException::new);

        isPasswordMatchingWithEncoded(secession.getPassword(), member.getPassword());
        memberRepository.delete(member);

    }

    @Override
    public boolean checkNicknameDuplication(String nickname) {
        return memberRepository.isNicknameDuplicate(nickname);
    }

    @Override
    @Transactional
    public String logout(String email, HttpServletResponse servletResponse) {
        cookieUtil.removeCookie("RefreshToken", servletResponse);
        refreshTokenRepository.findById(email)
                              .ifPresent(refreshTokenRepository::delete);
        return email;
    }

    private Member findMemberByEmail(String email) {
        Member member = memberRepository.findByEmail(email)
                                        .orElseThrow(EmailNotFoundException::new);
        log.info("event=MemberFindByEmail, email={}", email);
        return member;
    }

    private void removeOldRefreshToken(LoginRequestDto requestDto, Member member) {
        refreshTokenRepository.findById(member.getEmail())
                              .ifPresent(refreshTokenRepository::delete);
        log.info("event=DeleteExistingRefreshToken, email={}", requestDto.getEmail());
    }

    private void throwDuplicateEmailException(Member member) {
        throw new DuplicateEmailException();
    }

    private void isPasswordMatchingWithEncoded(String input, String encoded) {
        if (!passwordEncoder.matches(input, encoded)) {
            throw new InvalidLoginAttemptException();
        }
    }

    private void checkPasswordConfirmation(String password, String passwordConfirm) {
        if (!password.equals(passwordConfirm)) {
            throw new PasswordMismatchException();
        }
    }
}
