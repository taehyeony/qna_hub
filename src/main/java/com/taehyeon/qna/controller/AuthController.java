package com.taehyeon.qna.controller;

import com.taehyeon.qna.dto.request.SignInRequestDto;
import com.taehyeon.qna.dto.response.ApiResponse;
import com.taehyeon.qna.dto.response.SignInResponseDto;
import com.taehyeon.qna.dto.response.SignInTokenResponse;
import com.taehyeon.qna.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<SignInResponseDto>> signUp(
            @RequestBody SignInRequestDto signInRequestDto,
            HttpServletResponse response){
        SignInTokenResponse tokens = authService.signIn(signInRequestDto);

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", tokens.getRefreshToken())
                .httpOnly(true)    // JS 접근 불가 (XSS 방지)
                .secure(true)      // HTTPS 환경에서만 전송 (단, 로컬 개발 시엔 False)
                .path("/")         // 모든 경로에서 전송
                .maxAge(14 * 24 * 60 * 60) // 14일 (초 단위)
                .sameSite("Lax")   // CSRF 방지 (최근 브라우저 기본값)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
        SignInResponseDto responseDto = SignInResponseDto.builder().accessToken(tokens.getAccessToken()).build();
        ApiResponse<SignInResponseDto> apiResponse = ApiResponse.success(responseDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }
}
