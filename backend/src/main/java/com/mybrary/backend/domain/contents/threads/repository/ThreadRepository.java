package com.mybrary.backend.domain.contents.threads.repository;

import com.mybrary.backend.domain.contents.threads.entity.Threads;

import com.mybrary.backend.domain.contents.threads.repository.custom.ThreadRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThreadRepository extends JpaRepository<Threads, Long>,
    ThreadRepositoryCustom {

}
