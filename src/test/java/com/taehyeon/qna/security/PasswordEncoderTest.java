package com.taehyeon.qna.security;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@Tags({
        @Tag("unit"),
        @Tag("global")
})
@RequiredArgsConstructor
public class PasswordEncoderTest {
    private final PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("GLB-U-01 | PasswordEncoder 암호화")
    void passwordEncoder_test(){
        //given
        final String rawPassword = "test@1234";

        //when
        String firstEncodedPassword = passwordEncoder.encode(rawPassword);
        String secondEncodedPassword = passwordEncoder.encode(rawPassword);

        //then
        assertThat(firstEncodedPassword).isNotEqualTo(rawPassword); // 평문이 노

    }
}
