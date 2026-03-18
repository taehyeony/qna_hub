package com.taehyeon.qna.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

/**
 * 회원 일반 정보 수정 입력 DTO
 */
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateSimpleProfileRequestDto {
    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    @Pattern(
            regexp = "^(?!\\d+$)[a-zA-Z0-9가-힣]{2,10}$",
            message = "닉네임은 2~10자이며, 한글, 영문, 숫자만 가능하며, 숫자만으로 구성될 수 없습니다."
    )
    private String nickname;
}
