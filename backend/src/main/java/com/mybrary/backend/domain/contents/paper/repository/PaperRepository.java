package com.mybrary.backend.domain.contents.paper.repository;

import com.mybrary.backend.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaperRepository extends JpaRepository<Member, Long> {

}
