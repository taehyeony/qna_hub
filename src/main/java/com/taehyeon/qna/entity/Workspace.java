package com.taehyeon.qna.entity;

import com.taehyeon.qna.util.UuidUtil;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Workspace 엔티티
 * - 작업 환경 정보를 관리함
 * 
 * - type
 * - - ORIGIN: 원본 workspace(질문자가 최초로 등록한 질문 workspace)
 * - - ARCHIVE: 원본 workspace의 복제본(답변자가 답변을 생성한 workspace)
 * - - SNAPSHOT: 수정될 여지가 없는 완성본(채택이 완료된 답변의 workspace)
 */
@Entity
@Table(name = "workspace")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Workspace extends BaseMutableTimeEntity {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(length = 20, nullable = false)
    private String type;

    @Builder
    public Workspace(String type){
        this.id = UuidUtil.createUuidV7();
        this.type = type;
    }
}
