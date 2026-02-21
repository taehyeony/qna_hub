package com.taehyeon.qna.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

/**
 * 회원가입 입력 DTO
 * 
 * 이메일: 이메일 형식
 * 비밀번호: 영문 대/소문자 + 숫자 + 특수문자 (8~20자)
 * 닉네임: 한글/영문/숫자 조합 (2~10자, 숫자만 입력 불가)
 */
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SignUpRequestDto {
    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$",
            message = "비밀번호는 8~20자이며, 대문자, 소문자, 숫자, 특수문자를 모두 포함해야 합니다."
    )
    private String password;

    @NotBlank(message = "비밀번호 확인은 필수 입력 값입니다.")
    private String passwordCheck;
    
    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    @Pattern(
            regexp = "^(?!\\d+$)[a-zA-Z0-9가-힣]{2,10}$",
            message = "닉네임은 2~10자이며, 한글, 영문, 숫자만 가능하며, 숫자만으로 구성될 수 없습니다."
    )
    private String nickname;
}
