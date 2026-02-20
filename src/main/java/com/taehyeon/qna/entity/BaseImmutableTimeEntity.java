package com.taehyeon.qna.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 생성일, 삭제일
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseImmutableTimeEntity {
    public static final LocalDateTime NOT_DELETED_DATE = LocalDateTime.of(2000, 1, 1, 0, 0, 1);

    @CreatedDate
    @Column(updatable = false,nullable = false)
    protected LocalDateTime createdAt;

    @Column(nullable = false)
    protected LocalDateTime deletedAt;

    @PrePersist
    public void prePersist() {
        if (this.deletedAt == null) {
            this.deletedAt = NOT_DELETED_DATE;
        }
    }
}
