package com.taehyeon.qna.service;

import com.taehyeon.qna.dto.request.SignUpRequestDto;
import com.taehyeon.qna.entity.BaseImmutableTimeEntity;
import com.taehyeon.qna.entity.User;
import com.taehyeon.qna.enums.ErrorCode;
import com.taehyeon.qna.exception.BusinessException;
import com.taehyeon.qna.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * 회원가입
     * @param signUpRequestDto SignUpRequestDto 객체
     */
    @Transactional
    public void signUp(SignUpRequestDto signUpRequestDto) {
        // 1. 비밀번호 일치 여부 검증
        if (!signUpRequestDto.getPassword().equals(signUpRequestDto.getPasswordCheck())) {
            throw new BusinessException(ErrorCode.PASSWORD_CHECK_MISMATCH);
        }

        // 1. 이메일 중복 및 복구 유도
        userRepository.findByEmail(signUpRequestDto.getEmail())
                .ifPresent(user->{
                    if(!user.getDeletedAt().equals(BaseImmutableTimeEntity.NOT_DELETED_DATE)){
                        // 탈퇴 예정인 이메일인 경우 계정 복구 예외 발생
                        throw new BusinessException(ErrorCode.RECOVERY_REQUIRED);
                    }
                    // 이미 이메일이 존재하면 중복 이메일 예외 발생
                    throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
                });
        
        // 2. 닉네임 중복 체크
        if(userRepository.existsByNickname(signUpRequestDto.getNickname())){
            throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
        }
        
        // 3. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(signUpRequestDto.getPassword());
        
        // 4. 유저 엔티티 생성 및 저장
        User user = User.builder()
                .email(signUpRequestDto.getEmail())
                .password(passwordEncoder.encode(signUpRequestDto.getPassword()))
                .nickname(signUpRequestDto.getNickname())
                .build();

        userRepository.save(user);
    }
}
