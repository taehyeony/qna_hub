package com.taehyeon.qna.config;

import com.taehyeon.qna.util.UuidUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtProviderTests {
    @InjectMocks
    private JwtProvider jwtProvider;

    @Mock
    private StringRedisTemplate redisTemplate;


    // 테스트용 상수 설정
    private final String secretKey = "v7-taehyeon-qna-service-very-long-secret-key-2026";
    private final long accessTokenValidity = 1800000L; // 30분
    private final long refreshTokenValidity = 604800000L; // 7일

    @BeforeEach
    void setUp() {
        // Reflection을 사용하거나, 직접 필드에 값을 꽂아줍니다 (@Value 모사)
        ReflectionTestUtils.setField(jwtProvider, "secretKey", secretKey);
        ReflectionTestUtils.setField(jwtProvider, "accessTokenValidity", accessTokenValidity);
        ReflectionTestUtils.setField(jwtProvider, "refreshTokenValidity", refreshTokenValidity);

        // @PostConstruct 역할을 하는 init() 메서드 호출
        jwtProvider.init();
    }


    @Test
    @DisplayName("토큰 생성 및 검증 성공")
    void createToken_Success(){
        //given
        UUID userUuid = UuidUtil.createUuidV7();
        given(redisTemplate.hasKey(anyString())).willReturn(false);

        //when
        String accessToken = jwtProvider.createAccessToken(userUuid);
        String refreshToken = jwtProvider.createRefreshToken(userUuid);

        //then
        assertThat(accessToken).isNotNull();
        assertThat(jwtProvider.validateToken(accessToken)).isTrue();
        assertThat(refreshToken).isNotNull();
        assertThat(jwtProvider.validateToken(refreshToken)).isTrue();
        assertThat(jwtProvider.getSubject(accessToken)).isEqualTo(userUuid);
    }


    @Test
    @DisplayName("실패 - 변조된 토큰으로 접근 시")
    void validateToken_Fail_Tampered(){
        //given
        UUID userUuid = UuidUtil.createUuidV7();
        String accessToken = jwtProvider.createAccessToken(userUuid);
        String tamperedToken = accessToken + "X";

        //when
        boolean isValid = jwtProvider.validateToken(tamperedToken);

        //then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("실패 - 만료된 토큰으로 접근 시")
    void validationToken_Fail_Expired(){
        //given
        //임의로 유효기간이 0인 Provider 생성
        JwtProvider expiredProvider = new JwtProvider(redisTemplate);
        ReflectionTestUtils.setField(expiredProvider, "secretKey", secretKey);
        ReflectionTestUtils.setField(expiredProvider, "accessTokenValidity", -1000L); // 이미 만료됨
        ReflectionTestUtils.setField(expiredProvider, "redisTemplate", redisTemplate);
        expiredProvider.init();

        String expiredToken = expiredProvider.createAccessToken(UuidUtil.createUuidV7());

        //when
        boolean isValid = jwtProvider.validateToken(expiredToken);

        //then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Redis BlackList에 등록된 토큰으로 접근 시")
    void validateToken_Fail_BlackList(){
        //given
        UUID userUuid = UuidUtil.createUuidV7();
        String accessToken = jwtProvider.createAccessToken(userUuid);

        //Redis에 accessToken이 존재한다고 가정
        given(redisTemplate.hasKey(accessToken)).willReturn(true);

        //when
        boolean isValid = jwtProvider.validateToken(accessToken);

        //then
        assertThat(isValid).isFalse();
    }

}
