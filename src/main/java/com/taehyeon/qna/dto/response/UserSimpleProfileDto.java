package com.taehyeon.qna.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSimpleProfileDto {
    private UUID id;
    private String email;
    private String nickname;
    private int point;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
