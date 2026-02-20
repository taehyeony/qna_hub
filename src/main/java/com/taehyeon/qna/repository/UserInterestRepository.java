package com.taehyeon.qna.repository;

import com.taehyeon.qna.entity.UserInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserInterestRepository extends JpaRepository<UserInterest, UUID> {
}
