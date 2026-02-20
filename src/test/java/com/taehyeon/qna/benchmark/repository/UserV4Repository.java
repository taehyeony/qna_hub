package com.taehyeon.qna.benchmark.repository;

import com.taehyeon.qna.benchmark.entity.UserV4;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserV4Repository extends JpaRepository<UserV4, UUID> {
}
