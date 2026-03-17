package com.taehyeon.qna.service;

import com.taehyeon.qna.dto.request.SignUpRequestDto;
import com.taehyeon.qna.dto.response.UserSimpleProfileDto;
import com.taehyeon.qna.entity.User;
import com.taehyeon.qna.enums.ErrorCode;
import com.taehyeon.qna.exception.BusinessException;
import com.taehyeon.qna.repository.UserRepository;
import com.taehyeon.qna.util.UuidUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class UserServiceTests {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Nested
    @DisplayName("회원가입 테스트")
    class SignUp {
        @Test
        @DisplayName("회원가입 성공")
        void signUp_Success(){
            //given
            SignUpRequestDto signUpRequestDto = createSignUpRequestBuilder().build();

            //when
            userService.signUp(signUpRequestDto);

            //then
            Optional<User> savedUserOptional = userRepository.findByEmail(signUpRequestDto.getEmail());
            assertThat(savedUserOptional).isPresent();
            User savedUser = savedUserOptional.get();
            assertThat(savedUser.getNickname()).isEqualTo(signUpRequestDto.getNickname());
            assertThat(passwordEncoder.matches(signUpRequestDto.getPassword(), savedUser.getPassword())).isTrue();
        }

        @Test
        @DisplayName("회원가입 실패 - 비밀번호와 비밀번호 확인이 다른 경우")
        void signUp_Failed_PasswordCheckMissMatch(){
            //given
            SignUpRequestDto signUpRequestDto = createSignUpRequestBuilder()
                    .passwordCheck("incorrectPassword")
                    .build();

            //when & then
            //비밀번호 확인이 불일치하는 경우 errorCode가 PASSWORD_CHECK_MISSMATCH가 반환되는지 검증
            assertThatThrownBy(() -> userService.signUp(signUpRequestDto))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PASSWORD_CHECK_MISMATCH);
        }

        @Test
        @DisplayName("회원가입 실패 - 이미 존재하는 이메일")
        void signUp_Failed_DuplicateEmail(){
            //given
            User existingUser = User.builder()
                    .email("test@test.com")
                    .password("password")
                    .nickname("기존유저")
                    .build();
            userRepository.save(existingUser);

            SignUpRequestDto signUpRequestDto = createSignUpRequestBuilder().build();

            //when & then
            assertThatThrownBy(() -> userService.signUp(signUpRequestDto))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_EMAIL);
        }

        @Test
        @DisplayName("회원가입 실패 - 탈퇴 유예 기간 내 이메일 (계정 복구 유도)")
        void signUp_Failed_RecoveryRequired(){
            //given
            User deletedUser = User.builder()
                    .email("test@test.com")
                    .password("password")
                    .nickname("탈퇴유저")
                    .build();
            ReflectionTestUtils.setField(deletedUser, "deletedAt", LocalDateTime.now());
            userRepository.save(deletedUser);

            SignUpRequestDto signUpRequestDto = createSignUpRequestBuilder().build();

            //when & then
            //탈퇴 예정이라 soft delete인 계정의 경우 계정 복구 예외 발생 검증
            assertThatThrownBy(() -> userService.signUp(signUpRequestDto))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RECOVERY_REQUIRED);
        }

        @Test
        @DisplayName("회원가입 실패 - 이미 존재하는 닉네임")
        void signUp_Failed_DuplicateNickname(){
            //given
            User existingUser = User.builder()
                    .email("anotherTest@test.com")
                    .password("password")
                    .nickname("테스트유저1")
                    .build();
            userRepository.save(existingUser);

            SignUpRequestDto signUpRequestDto = createSignUpRequestBuilder().build();

            //when & then
            assertThatThrownBy(() -> userService.signUp(signUpRequestDto))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_NICKNAME);
        }

        private SignUpRequestDto.SignUpRequestDtoBuilder createSignUpRequestBuilder(){
            return SignUpRequestDto.builder()
                    .email("test@test.com")
                    .password("Password123!")
                    .passwordCheck("Password123!")
                    .nickname("테스트유저1");
        }
    }

    @Nested
    @DisplayName("내 단순 프로필 조회")
    class ViewMyProfile {
        private User savedUser;

        @BeforeEach
        void setup(){
            User user = User.builder()
                    .email("test@test.com")
                    .password(passwordEncoder.encode("Password123!"))
                    .nickname("테스트유저1")
                    .build();
            savedUser = userRepository.save(user);
        }

        @Test
        @DisplayName("단순 프로필 조회 성공")
        void getSimpleProfile_Success(){
            //when
            UserSimpleProfileDto responseDto = userService.getMySimpleProfile(savedUser.getId());

            //then
            assertThat(responseDto).isNotNull();
            assertThat(responseDto.getEmail()).isEqualTo("test@test.com");
            assertThat(responseDto.getNickname()).isEqualTo("테스트유저1");
            assertThat(responseDto.getId()).isEqualTo(savedUser.getId());
        }

        @Test
        @DisplayName("단순 프로필 조회 실패 - 존재하지 않는 유저")
        void getSimpleProfile_Failed_NotFoundUser(){
            //given
            UUID notExistentId = UuidUtil.createUuidV7();

            //when & then
            assertThatThrownBy(() -> userService.getMySimpleProfile(notExistentId))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
        }
    }

}


