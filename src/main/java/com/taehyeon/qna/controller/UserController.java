package com.taehyeon.qna.controller;

import com.taehyeon.qna.dto.request.SignUpRequestDto;
import com.taehyeon.qna.dto.response.ApiResponse;
import com.taehyeon.qna.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signUp(@RequestBody SignUpRequestDto signUpRequestDto){
        System.out.println("컨트롤러 진입 확인");
        userService.signUp(signUpRequestDto);

        ApiResponse<Void> response = ApiResponse.success(null);
        System.out.println("응답 객체 확인: " + response.getMessage()); // 콘솔에 찍히는지 확인

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
