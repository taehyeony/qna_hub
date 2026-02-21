package com.taehyeon.qna.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.taehyeon.qna.enums.ErrorCode;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private final boolean success;
    private final int status;
    private final String message;
    private final String errorCode;
    private final T data; // 성공 시 데이터
    private final List<String> errors; // 유효성 검사 실패 시 필드별 상세 에러

    // 성공 응답 (데이터가 있는 경우)
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(HttpStatus.OK.value())
                .message("요청이 성공적으로 처리되었습니다.")
                .data(data)
                .build();
    }

    // 에러 응답 (비즈니스 예외)
    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return ApiResponse.<T>builder()
                .success(false)
                .status(errorCode.getStatus().value())
                .message(errorCode.getMessage())
                .errorCode(errorCode.getCode())
                .build();
    }

    // 에러 응답 (유효성 검사 실패 등 상세 에러 포함)
    public static <T> ApiResponse<T> error(ErrorCode errorCode, List<String> errors) {
        return ApiResponse.<T>builder()
                .success(false)
                .status(errorCode.getStatus().value())
                .message(errorCode.getMessage())
                .errorCode(errorCode.getCode())
                .errors(errors)
                .build();
    }
}
