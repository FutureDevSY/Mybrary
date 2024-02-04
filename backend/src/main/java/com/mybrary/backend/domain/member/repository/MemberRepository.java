package com.mybrary.backend.domain.member.repository;

import com.mybrary.backend.domain.follow.entity.Follow;
import com.mybrary.backend.domain.member.entity.Member;
import com.mybrary.backend.domain.member.repository.custom.QuerydslMemberRepository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long>, QuerydslMemberRepository {

    @Query("select m from Member m inner join Follow f on m.id = f.following.id where f.follower.id = :myId and f.isDeleted = false ")
    List<Member> getAllFollowing(@Param("myId") Long myId);

    @Query("select m from Member m inner join Follow f on m.id = f.follower.id where f.following.id = :myId and f.isDeleted = false ")
    List<Member> getAllFollower(@Param("myId") Long myId);

    @Query("select f from Follow f where f.following.id = :memberId and f.follower.id = :myId and f.isDeleted = false ")
    Follow isFollowed(@Param("myId") Long myId, @Param("memberId") Long memberId);

}
