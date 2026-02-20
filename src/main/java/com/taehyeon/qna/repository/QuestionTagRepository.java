package com.taehyeon.qna.repository;

import com.taehyeon.qna.entity.Question;
import com.taehyeon.qna.entity.QuestionTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface QuestionTagRepository extends JpaRepository<QuestionTag, UUID> {
}
