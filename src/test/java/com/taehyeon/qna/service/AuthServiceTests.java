package com.taehyeon.qna.service;

import com.taehyeon.qna.config.JwtProvider;
import com.taehyeon.qna.dto.request.SignInRequestDto;
import com.taehyeon.qna.dto.response.SignInTokenResponse;
import com.taehyeon.qna.entity.BaseImmutableTimeEntity;
import com.taehyeon.qna.entity.User;
import com.taehyeon.qna.enums.ErrorCode;
import com.taehyeon.qna.exception.BusinessException;
import com.taehyeon.qna.repository.UserRepository;
import com.taehyeon.qna.util.UuidUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.BDDMockito.*;

@SpringBootTest
@Transactional
public class AuthServiceTests {
    @Autowired
    private AuthService authService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private JwtProvider jwtProvider;

    private User existingUser;
    private static final String correctEmail = "test@email.com";
    private static final String correctPassword = "Password1234!";

    @BeforeEach
    void setup(){
        User user = User.builder()
                .email(correctEmail)
                .password(passwordEncoder.encode(correctPassword))
                .nickname("테스트 유저")
                .build();
        existingUser = userRepository.save(user);
    }


    @Nested
    @DisplayName("로그인 테스트")
    class SignInTests {

        @Test
        @DisplayName("로그인 성공")
        void signIn_Success(){
            //given
            SignInRequestDto signInRequestDto = createSignInRequestBuilder(correctEmail,correctPassword).build();

            //when
            SignInTokenResponse responseDto = authService.signIn(signInRequestDto);

            //then
            assertThat(jwtProvider.validateToken(responseDto.getAccessToken())).isTrue();
            assertThat(jwtProvider.validateToken(responseDto.getRefreshToken())).isTrue();

            UUID userId = jwtProvider.getSubject(responseDto.getAccessToken());
            assertThat(userId).isEqualTo(existingUser.getId());
        }

        @ParameterizedTest
        @MethodSource("provideInvalidCredentials")
        @DisplayName("로그인 실패 - 계정 정보 불일치 (보안을 위해 동일 에러 반환 검증)")
        void signIn_Failed_InvalidCredentials(String inputEmail, String inputPassword){
            //given
            SignInRequestDto signInRequestDto = createSignInRequestBuilder(inputEmail,inputPassword).build();

            //when
            Exception exception = catchException(() -> authService.signIn(signInRequestDto));

            //then
            assertThat(exception)
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_LOGIN_CREDENTIALS);
        }

        @Test
        @DisplayName("로그인 실패 - 탈퇴 유예 기간 계정 (로그인 차단 후 복구 유도)")
        void signIn_Failed_DeletedUser(){
            //given
            ReflectionTestUtils.setField(existingUser,"deletedAt", LocalDateTime.now());
            userRepository.saveAndFlush(existingUser);

            SignInRequestDto signInRequestDto = createSignInRequestBuilder(correctEmail,correctPassword).build();

            //when
            Exception exception = catchException(()->authService.signIn(signInRequestDto));

            //then
            assertThat(exception)
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RECOVERY_REQUIRED);
        }

        /**
         * 로그인 실패 시나리오 제공
         * 1. 이메일은 맞지만 비밀번호가 틀린 경우
         * 2. 존재하지 않는 이메일인 경우
         * @return {String inputEmail, String inputPassword} 형태의 Arguments 스트림
         */
        private static Stream<Arguments> provideInvalidCredentials() {
            return Stream.of(
                    // Arguments.of(입력 이메일, 입력 비밀번호)
                    Arguments.of(correctEmail,correctPassword + "s"),
                    Arguments.of(correctEmail+"s",correctPassword)
            );
        }
    }

    // 로그인 요청 객체 생성 util 함수
    private SignInRequestDto.SignInRequestDtoBuilder createSignInRequestBuilder(String email, String encodedPassword) {
        return SignInRequestDto.builder()
                .email(email)
                .password(encodedPassword);
    }
}


