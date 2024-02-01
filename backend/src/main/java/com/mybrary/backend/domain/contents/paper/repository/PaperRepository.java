package com.mybrary.backend.domain.contents.paper.repository;

import com.mybrary.backend.domain.contents.paper.entity.Paper;
import com.mybrary.backend.domain.contents.paper.repository.custom.QuerydslPaperRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaperRepository extends JpaRepository<Paper, Long>, QuerydslPaperRepository {

    public Long deleteAllByThreadId(Long threadId);
}
