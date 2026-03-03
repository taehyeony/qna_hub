package com.taehyeon.qna.service;

import com.taehyeon.qna.config.JwtProvider;
import com.taehyeon.qna.dto.request.SignInRequestDto;
import com.taehyeon.qna.dto.response.SignInTokenResponse;
import com.taehyeon.qna.entity.BaseImmutableTimeEntity;
import com.taehyeon.qna.entity.User;
import com.taehyeon.qna.enums.ErrorCode;
import com.taehyeon.qna.exception.BusinessException;
import com.taehyeon.qna.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final StringRedisTemplate redisTemplate;

    /**
     * 로그인
     * @param signInRequestDto
     * @return
     */
    public SignInTokenResponse signIn(SignInRequestDto signInRequestDto) {
        
        //1. 이메일 존재 확인
        User user = userRepository.findByEmail(signInRequestDto.getEmail())
                .orElseThrow(()-> new BusinessException(ErrorCode.INVALID_LOGIN_CREDENTIALS));

        //2. 비밀번호 일치 확인
        if(!passwordEncoder.matches(signInRequestDto.getPassword(), user.getPassword())){
            throw new BusinessException(ErrorCode.INVALID_LOGIN_CREDENTIALS);
        }

        //3. 탈퇴 유예 여부 확인
        if(!user.getDeletedAt().equals(BaseImmutableTimeEntity.NOT_DELETED_DATE)){
            throw new BusinessException(ErrorCode.RECOVERY_REQUIRED);
        }

        //4. 토큰 생성
        String accessToken = jwtProvider.createAccessToken(user.getId());
        String refreshToken = jwtProvider.createRefreshToken(user.getId());

        // 5. Redis에 Refresh 토큰 저장
        redisTemplate.opsForValue().set(
                "RT:" + user.getId(),
                refreshToken,
                14, TimeUnit.DAYS
        );

        return SignInTokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
