package com.taehyeon.qna.config;

import com.taehyeon.qna.util.UuidUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@SpringBootTest
public class JwtProviderTests {
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("${jwt.secret}")
    private String secretKey;

    @AfterEach
    void tearDown() {
        redisTemplate.execute((RedisCallback<Object>) connection -> {
            connection.serverCommands().flushDb();
            return null;
        });
    }


    @Test
    @DisplayName("토큰 생성 및 검증 성공")
    void createToken_Success(){
        //given
        UUID userUuid = UuidUtil.createUuidV7();

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
        ReflectionTestUtils.setField(expiredProvider,"secretKey", secretKey);
        ReflectionTestUtils.setField(expiredProvider, "accessTokenValidity", -1000L);
        ReflectionTestUtils.setField(expiredProvider, "refreshTokenValidity", -1000L);
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

        redisTemplate.opsForValue().set(accessToken, "blacklist", 30, TimeUnit.MINUTES);

        //when
        boolean isValid = jwtProvider.validateToken(accessToken);

        //then
        assertThat(isValid).isFalse();
    }

}
