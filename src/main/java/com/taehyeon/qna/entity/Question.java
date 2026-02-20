package com.taehyeon.qna.entity;

import com.taehyeon.qna.util.UuidUtil;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "question")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Question extends BaseMutableTimeEntity {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private int viewCount = 0;

    @Column(length = 20, nullable = false)
    private String status = "OPEN";

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_workspace_id", nullable = false, unique = true)
    private Workspace originWorkSpace;

    @Builder
    public Question(User author, String title, String content, Workspace originWorkSpace){
        this.id = UuidUtil.createUuidV7();
        this.author = author;
        this.title = title;
        this.content = content;
        this.originWorkSpace = originWorkSpace;
    }
}