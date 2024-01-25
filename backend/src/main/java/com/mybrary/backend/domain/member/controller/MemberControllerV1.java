package com.mybrary.backend.domain.member.controller;

import com.mybrary.backend.domain.member.dto.FollowerDto;
import com.mybrary.backend.domain.member.dto.FollowingDto;
import com.mybrary.backend.domain.member.dto.LoginRequestDto;
import com.mybrary.backend.domain.member.dto.MemberUpdateDto;
import com.mybrary.backend.domain.member.dto.MyFollowerDto;
import com.mybrary.backend.domain.member.dto.MyFollowingDto;
import com.mybrary.backend.domain.member.dto.PasswordUpdateDto;
import com.mybrary.backend.domain.member.dto.SecessionRequestDto;
import com.mybrary.backend.domain.member.dto.SignupRequestDto;
import com.mybrary.backend.domain.member.service.MemberService;
import com.mybrary.backend.global.format.ApiResponse;
import com.mybrary.backend.global.format.ResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Member 컨트롤러", description = "Member Controller API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberControllerV1 {

    private final ApiResponse response;
    private final MemberService memberService;

    @Operation(summary = "일반 회원가입", description = "일반 회원가입")
    @PostMapping
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequestDto requestDto,
                                    BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return response.fail(bindingResult);
        }

        return response.success(ResponseCode.MEMBER_SIGNUP_SUCCESS.getMessage(), requestDto);
    }

    @Operation(summary = "소셜 회원가입", description = "소셜 회원가입")
    @PostMapping("/social")
    public ResponseEntity<?> signupBySocial(@Valid @RequestBody SignupRequestDto requestDto,
                                            BindingResult bindingResult) {
        return response.success(ResponseCode.MEMBER_SIGNUP_SUCCESS.getMessage());
    }

    @Operation(summary = "이메일 인증 요청", description = "이메일 주소 보내고 인증코드를 메일로 보내는 요청")
    @PostMapping("/email/verification")
    public ResponseEntity<?> emailVerification(@RequestParam String email) {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "인증코드 인증 요청", description = "메일로 받은 인증코드를 입력해서 인증 요청")
    @PostMapping("/email/verify")
    public ResponseEntity<?> emailVerify(@RequestParam String verificationCode) {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "닉네임 검사", description = "닉네임 유효성 및 중복 검사")
    @GetMapping("/nickname")
    public ResponseEntity<?> nicknameCheck(@RequestParam String nickname) {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "일반 로그인", description = "일반 로그인")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto member) {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "소셜 로그인", description = "소셜 로그인")
    @PostMapping("/login/social")
    public ResponseEntity<?> loginBySocial(@RequestBody LoginRequestDto member) {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "비밀번호 재설정(비번찾기)", description = "비밀번호를 잃어버렸을 때 비밀번호 재설정")
    @PutMapping("/password-reset")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordUpdateDto password) {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "비밀번호 재설정(로그인후)", description = "로그인 후 비밀번호 재설정")
    @PutMapping("/password-update")
    public ResponseEntity<?> updatePassword(@RequestBody PasswordUpdateDto password) {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "로그아웃", description = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "프로필 수정", description = "닉네임, 프로필이미지, 소개 수정")
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody MemberUpdateDto member) {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "나의 팔로잉 리스트", description = "나의 팔로잉 리스트")
    @GetMapping("/me/followings")
    public ResponseEntity<?> getAllMyFollowing() {
        return new ResponseEntity<List<MyFollowingDto>>(HttpStatus.OK);
    }

    @Operation(summary = "나의 팔로워 리스트", description = "나의 팔로워 리스트")
    @GetMapping("/me/followers")
    public ResponseEntity<?> getAllMyFollower() {
        return new ResponseEntity<List<MyFollowerDto>>(HttpStatus.OK);
    }

    @Operation(summary = "특정회원의 팔로잉 리스트", description = "특정회원의 팔로잉 리스트")
    @GetMapping("/{id}/followings")
    public ResponseEntity<?> getAllFollowing(@PathVariable(name = "id") Long memberId) {
        return new ResponseEntity<List<FollowingDto>>(HttpStatus.OK);
    }

    @Operation(summary = "특정회원의 팔로워 리스트", description = "특정회원의 팔로워 리스트")
    @GetMapping("/{id}/followers")
    public ResponseEntity<?> getAllFollower(@PathVariable(name = "id") Long memberId) {
        return new ResponseEntity<List<FollowerDto>>(HttpStatus.OK);
    }

    @Operation(summary = "팔로우하기", description = "특정회원을 팔로우하기")
    @PostMapping("/{id}/follow")
    public ResponseEntity<?> follow(@PathVariable(name = "id") Long memberId) {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "언팔로우하기", description = "특정회원을 언팔로우하기")
    @DeleteMapping("/{id}/unfollow")
    public ResponseEntity<?> unfollow(@PathVariable(name = "id") Long memberId) {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "팔로워끊기", description = "특정회원이 나를 팔로우한 것을 끊기")
    @DeleteMapping("/{id}/follower")
    public ResponseEntity<?> deleteFollower(@PathVariable(name = "id") Long memberId) {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "계정 탈퇴", description = "계정 탈퇴 (삭제처리)")
    @DeleteMapping("/secession")
    public ResponseEntity<?> secession(@RequestBody SecessionRequestDto secession) {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "계정 공개여부 설정", description = "계정 공개/비공개")
    @PutMapping("/privacy")
    public ResponseEntity<?> accountPrivacy() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "계정 알림 설정", description = "계정 알림 설정")
    @PutMapping("/notify")
    public ResponseEntity<?> accountNotify() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

}

