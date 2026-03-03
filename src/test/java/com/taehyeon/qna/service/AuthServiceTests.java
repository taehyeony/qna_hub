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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTests {
    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    private User existingUser;
    private static final String correctEmail = "test@email.com";
    private static final String correctPassword = "Password1234!";

    /**
     * 각 테스트 실행 전, 이미 회원가입된 유저 객체를 생성하고, 필드를 초기화합니다.
     */
    @BeforeEach
    void setup(){
        existingUser = User.builder()
                .email(correctEmail)
                .password(correctPassword+"encoded")
                .build();
        // JPA가 영속화 하기 전에 주입하는 ID와 공통 엔티티 필드를 수동으로 주입한다.
        ReflectionTestUtils.setField(existingUser, "id", UuidUtil.createUuidV7());
        ReflectionTestUtils.setField(existingUser, "deletedAt", BaseImmutableTimeEntity.NOT_DELETED_DATE);
    }


    @Nested
    @DisplayName("로그인 테스트")
    class SignInTests {

        @Test
        @DisplayName("로그인 성공")
        void signIn_Success(){
            //given 정상적인 이메일과 비밀번호를 담은 로그인 요청
            SignInRequestDto signInRequestDto = createSignInRequestBuilder(correctEmail,correctPassword).build();

            // DB 조회 성공, 비밀번호 일치, 토큰 발급 성공을 가정
            given(userRepository.findByEmail(signInRequestDto.getEmail())).willReturn(Optional.of(existingUser));
            given(passwordEncoder.matches(eq(correctPassword),anyString())).willReturn(true);
            given(jwtProvider.createAccessToken(existingUser.getId())).willReturn("access-token");
            given(jwtProvider.createRefreshToken(existingUser.getId())).willReturn("refresh-token");

            //when 로그인 서비스 로직 실행
            SignInTokenResponse responseDto = authService.signIn(signInRequestDto);

            //then 발급된 토큰이 예상값과 일치하는지 검증
            assertThat(responseDto.getAccessToken()).isEqualTo("access-token");
            assertThat(responseDto.getRefreshToken()).isEqualTo("refresh-token");
        }

        @ParameterizedTest
        @MethodSource("provideInvalidCredentials")
        @MockitoSettings(strictness = Strictness.LENIENT)
        @DisplayName("로그인 실패 - 계정 정보 불일치 (보안을 위해 동일 에러 반환 검증)")
        void signIn_Failed_InvalidCredentials(String inputEmail, String inputPassword){
            //given 비밀번호나 이메일이 틀린 로그인 요청
            SignInRequestDto signInRequestDto = createSignInRequestBuilder(inputEmail,inputPassword).build();

            //이메일 일치 여부, 비밀번호 일치 여부 가정
            given(userRepository.findByEmail(anyString())).willAnswer(inv ->
                inv.getArgument(0).equals(correctEmail) ? Optional.of(existingUser) : Optional.empty()
            );
            given(passwordEncoder.matches(eq(correctPassword),anyString())).willReturn(true);

            //when 로그인 서비스 로직 실행
            Exception exception = catchException(() -> authService.signIn(signInRequestDto));

            //then 이메일이나 비밀번호가 틀린 경우 동일한 예외 반환 검증
            assertThat(exception)
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_LOGIN_CREDENTIALS);

            // 아이디가 틀린 경우에는 비밀번호 체크 로직이 실행되지 않는지 검증
            if(!inputEmail.equals(correctEmail)) {
                verify(passwordEncoder,never()).matches(anyString(),anyString());
            }
        }

        @Test
        @DisplayName("로그인 실패 - 탈퇴 유예 기간 계정 (로그인 차단 후 복구 유도)")
        void signIn_Failed_DeletedUser(){
            //given 탈퇴 유예 기간인 계정
            SignInRequestDto signInRequestDto = createSignInRequestBuilder(correctEmail,correctPassword).build();
            User deletedUser = existingUser;
            ReflectionTestUtils.setField(deletedUser,"deletedAt", LocalDateTime.now());

            // DB 조회 가정
            given(userRepository.findByEmail(signInRequestDto.getEmail())).willReturn(Optional.of(deletedUser));
            given(passwordEncoder.matches(eq(correctPassword),anyString())).willReturn(true);

            //when 로그인 서비스 로직 실행
            Exception exception = catchException(()->authService.signIn(signInRequestDto));

            //then 계정 복구 유도 예외 발생 검증
            assertThat(exception)
                    .isNotNull()
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


