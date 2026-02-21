package com.taehyeon.qna.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 공통
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "올바르지 않은 입력값입니다."),

    // 유저 관련
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "U001", "이미 존재하는 이메일입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "U002", "사용 중이거나 탈퇴 유예 기간인 닉네임입니다."),
    RECOVERY_REQUIRED(HttpStatus.FORBIDDEN, "U003", "탈퇴 유예 기간인 계정입니다. 복구 페이지로 이동하세요."),
    PASSWORD_CHECK_MISMATCH(HttpStatus.BAD_REQUEST,"U004", "비밀번호와 비밀번호 확인이 일치하지 않습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
