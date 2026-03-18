package com.taehyeon.qna.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.taehyeon.qna.entity.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateSimpleProfileResponseDto {
    private final String nickname;
    private final LocalDateTime updatedAt;

    public static UpdateSimpleProfileResponseDto from(User user){
        return UpdateSimpleProfileResponseDto.builder()
                .nickname(user.getNickname())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
