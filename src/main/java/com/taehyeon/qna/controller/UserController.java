package com.taehyeon.qna.controller;

import com.taehyeon.qna.dto.request.SignUpRequestDto;
import com.taehyeon.qna.dto.request.UpdatePasswordRequestDto;
import com.taehyeon.qna.dto.request.UpdateSimpleProfileRequestDto;
import com.taehyeon.qna.dto.response.ApiResponse;
import com.taehyeon.qna.dto.response.UpdateSimpleProfileResponseDto;
import com.taehyeon.qna.dto.response.UserSimpleProfileDto;
import com.taehyeon.qna.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signUp(@RequestBody @Valid SignUpRequestDto signUpRequestDto){
        userService.signUp(signUpRequestDto);
        ApiResponse<Void> response = ApiResponse.success(null);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserSimpleProfileDto>> getMySimpleProfile(
            @AuthenticationPrincipal UUID userId
    ){
        UserSimpleProfileDto response = userService.getMySimpleProfile(userId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<UpdateSimpleProfileResponseDto>> updateSimpleProfile(
            @AuthenticationPrincipal UUID userId,
            @RequestBody @Valid UpdateSimpleProfileRequestDto requestDto
    ){
        UpdateSimpleProfileResponseDto response = userService.updateSimpleProfile(userId, requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

    @PatchMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @AuthenticationPrincipal UUID userId,
            @RequestBody @Valid UpdatePasswordRequestDto requestDto
    ){
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(null));
    }
}
