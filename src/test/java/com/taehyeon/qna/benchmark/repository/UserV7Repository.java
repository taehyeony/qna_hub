package com.taehyeon.qna.benchmark.repository;

import com.taehyeon.qna.benchmark.entity.UserV7;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserV7Repository extends JpaRepository<UserV7, UUID> {
}
