package com.taehyeon.qna.controller;

import com.taehyeon.qna.dto.request.SignUpRequestDto;
import com.taehyeon.qna.dto.response.ApiResponse;
import com.taehyeon.qna.dto.response.UserSimpleProfileDto;
import com.taehyeon.qna.service.UserService;
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
    public ResponseEntity<ApiResponse<Void>> signUp(@RequestBody SignUpRequestDto signUpRequestDto){
        userService.signUp(signUpRequestDto);
        ApiResponse<Void> response = ApiResponse.success(null);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/myprofile")
    public ResponseEntity<ApiResponse<UserSimpleProfileDto>> getMySimpleProfile(
            @AuthenticationPrincipal UUID userId
    ){
        UserSimpleProfileDto response = userService.getMySimpleProfile(userId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }
}
