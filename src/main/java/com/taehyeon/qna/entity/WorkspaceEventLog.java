package com.taehyeon.qna.entity;

import com.taehyeon.qna.util.UuidUtil;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

/**
 * workspace 작업 로그 엔티티
 */
@Entity
@Table(name = "workspace_event_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class WorkspaceEventLog extends BaseImmutableTimeEntity {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 20, nullable = false)
    private String actionType;

    @Column(columnDefinition = "json", nullable = false)
    private String payload;

    @Column(nullable = false)
    private Long sequence;

    @Column(nullable = false)
    private boolean isUndone = false;

    @Builder
    public WorkspaceEventLog(Workspace workspace, User user, String actionType, String payload, Long sequence) {
        this.id = UuidUtil.createUuidV7();
        this.workspace = workspace;
        this.user = user;
        this.actionType = actionType;
        this.payload = payload;
        this.sequence = sequence;
    }
}
