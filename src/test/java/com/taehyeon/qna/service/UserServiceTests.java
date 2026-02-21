package com.taehyeon.qna.service;

import com.taehyeon.qna.dto.request.SignUpRequestDto;
import com.taehyeon.qna.entity.BaseImmutableTimeEntity;
import com.taehyeon.qna.entity.User;
import com.taehyeon.qna.enums.ErrorCode;
import com.taehyeon.qna.exception.BusinessException;
import com.taehyeon.qna.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원가입 성공")
    void signUp_Success(){
        //given
        SignUpRequestDto signUpRequestDto = createRequests();

        given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());
        given(userRepository.existsByNickname(anyString())).willReturn(false);
        given(passwordEncoder.encode(anyString())).willReturn("encoded_pw");

        //when & then
        //signUp이 예외를 던지지 않고 성공적으로 실행되는지 검증
        assertThatCode(() -> userService.signUp(signUpRequestDto))
                .doesNotThrowAnyException();

        //userRepository에서 save가 한번만 호출되었는지 검증.(파라미터는 User 클래스인 경우)
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 비밀번호와 비밀번호 확인이 다른 경우")
    void signUp_Failed_PasswordCheckMissMatch(){
        //given
        SignUpRequestDto signUpRequestDto = createRequests();
        ReflectionTestUtils.setField(signUpRequestDto,"passwordCheck", "incorrectPassword");

        //when & then
        //비밀번호 확인이 불일치하는 경우 errorCode가 PASSWORD_CHECK_MISSMATCH가 반환되는지 검증
        assertThatThrownBy(() -> userService.signUp(signUpRequestDto))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PASSWORD_CHECK_MISMATCH);

        //userRepository에서 save가 한번만 호출되었는지 검증.(파라미터는 User 클래스인 경우)
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 이미 존재하는 이메일")
    void signUp_Failed_DuplicateEmail(){
        //given
        SignUpRequestDto signUpRequestDto = createRequests();
        User existingUser = User.builder().email("test@test.com").build();
        ReflectionTestUtils.setField(existingUser, "deletedAt", BaseImmutableTimeEntity.NOT_DELETED_DATE);

        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(existingUser));

        //when & then
        assertThatThrownBy(() -> userService.signUp(signUpRequestDto))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_EMAIL);

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 탈퇴 유예 기간 내 이메일 (계정 복구 유도)")
    void signUp_Failed_RecoveryRequired(){
        //given
        SignUpRequestDto signUpRequestDto = createRequests();
        User deletedUser = User.builder().email("test@test.com").build();
        ReflectionTestUtils.setField(deletedUser, "deletedAt", LocalDateTime.now());

        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(deletedUser));

        //when & then
        //탈퇴 예정이라 soft delete인 계정의 경우 계정 복구 예외 발생 검증
        assertThatThrownBy(() -> userService.signUp(signUpRequestDto))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RECOVERY_REQUIRED);

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 이미 존재하는 닉네임")
    void signUp_Failed_DuplicateNickname(){
        //given
        SignUpRequestDto signUpRequestDto = createRequests();

        given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());
        given(userRepository.existsByNickname(anyString())).willReturn(true);

        //when & then
        assertThatThrownBy(() -> userService.signUp(signUpRequestDto))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_NICKNAME);

        verify(userRepository, never()).save(any(User.class));
    }

    private SignUpRequestDto createRequests(){
        return SignUpRequestDto.builder()
                .email("test@test.com")
                .password("Password123!")
                .passwordCheck("Password123!")
                .nickname("테스트유저1")
                .build();
    }
}


