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
public class UpdatePasswordRequestDto {
    @NotBlank(message = "현재 비밀번호는 필수 입력 값입니다.")
    private String currentPassword;
    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$",
            message = "비밀번호는 8~20자이며, 대문자, 소문자, 숫자, 특수문자를 모두 포함해야 합니다."
    )
    private String newPassword;
    @NotBlank(message = "비밀번호 확인은 필수 입력 값입니다.")
    private String newPasswordCheck;
}
