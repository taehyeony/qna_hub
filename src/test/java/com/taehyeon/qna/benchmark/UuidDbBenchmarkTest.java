//package com.taehyeon.qna.benchmark;
//
//import com.taehyeon.qna.benchmark.entity.UserV4;
//import com.taehyeon.qna.benchmark.entity.UserV7;
//import com.taehyeon.qna.benchmark.repository.UserV4Repository;
//import com.taehyeon.qna.benchmark.repository.UserV7Repository;
//import com.taehyeon.qna.util.UuidUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Tag;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.util.UUID;
//
//@SpringBootTest
//@ActiveProfiles("test") // 테스트용 DB 설정을 사용하도록 지정
//@Slf4j
//@Tag("benchmark")
//class UuidDbBenchmarkTest {
//
//    @Autowired
//    private UserV4Repository v4Repo;
//    @Autowired private UserV7Repository v7Repo;
//
////    @Test
////    @DisplayName("GLB-U01-B | DB 부하 측정: v4 vs v7 삽입 성능 비교")
////    void benchmark_DB_Insert() {
////        int count = 100000; // 데이터 10000 건
////
////        // 1. UUID v4 벤치마크
////        long v4Start = System.currentTimeMillis();
////        for (int i = 0; i < count; i++) {
////            v4Repo.save(new UserV4(UUID.randomUUID()));
////        }
////        long v4End = System.currentTimeMillis();
////
////        // 2. UUID v7 벤치마크
////        long v7Start = System.currentTimeMillis();
////        for (int i = 0; i < count; i++) {
////            v7Repo.save(new UserV7(UuidUtil.createUuidV7()));
////        }
////        long v7End = System.currentTimeMillis();
////
////        log.info("--- Benchmark Results : {} ---",count);
////        log.info("v4 Total Time: {}ms",v4End - v4Start);
////        log.info("v7 Total Time: {}ms",v7End - v7Start);
////    }
//
//    @Test
//    @DisplayName("GLB-U01-B | 생성 시간 측정: v4 vs v7 생성 성능 비교")
//    void benchmark_pure_generation() {
//        int count = 100_000_000;
//
//        // v4 생성만 측정
//        long v4Start = System.nanoTime();
//        for (int i = 0; i < count; i++) { UUID.randomUUID(); }
//        long v4End = System.nanoTime();
//
//        // v7 생성만 측정
//        long v7Start = System.nanoTime();
//        for (int i = 0; i < count; i++) { UuidUtil.createUuidV7(); }
//        long v7End = System.nanoTime();
//
//        System.out.println("Pure v4 Gen: " + (v4End - v4Start) / 100_000_000 + "ms");
//        System.out.println("Pure v7 Gen: " + (v7End - v7Start) / 100_000_000 + "ms");
//    }
//}