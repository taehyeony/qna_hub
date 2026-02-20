package com.taehyeon.qna.util;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;

import java.util.UUID;

public class UuidUtil {
    private static final TimeBasedEpochGenerator generator = Generators.timeBasedEpochGenerator();

    public static UUID createUuidV7() {
        return generator.generate();
    }
}
