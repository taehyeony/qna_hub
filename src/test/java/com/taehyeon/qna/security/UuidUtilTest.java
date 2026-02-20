package com.taehyeon.qna.security;

import com.taehyeon.qna.util.UuidUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Tags({
        @Tag("unit"),
        @Tag("global")
})
public class UuidUtilTest {
    @Test
    @DisplayName("GLB-U-03 | UUID v7 생성 및 정렬 검증")
    void uuidV7_test(){
        // given
        UUID firstUUID = UuidUtil.createUuidV7();
        UUID secondUUID = UuidUtil.createUuidV7();

        //then
        assertThat(firstUUID).isNotNull();
        assertThat(firstUUID.version()).isEqualTo(7); //UUID의 버전이 7인지 검증
        assertThat(firstUUID).isLessThan(secondUUID); // 처음 생성된 UUID가 이 후 생성된 UUID보다 작아야 한다.
        String regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-7[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$";
        assertThat(firstUUID.toString()).matches(regexp); // 생성된 UUID의 길이는 36자 (16진수 32자(128비트) + 하이픈 4자(구분자))
    }
}
