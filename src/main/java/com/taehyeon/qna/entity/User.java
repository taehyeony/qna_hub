package com.taehyeon.qna.entity;

import com.taehyeon.qna.util.UuidUtil;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 회원 엔티티
 * - 사용자의 인증 정보 및 활동 정보를 관리함.
 *
 * - 데이터 보존 및 삭제 정책
 * - - 논리 삭제: 탈퇴 시 deletedAt에 현재 시간을 기록하여 비활성화 처리.
 * - - 복구 가능: 탈퇴 신청 후 7일간 데이터를 보관하며, 이 기간 내에 계정 복구가 가능.
 * - - 물리 삭제: 탈퇴 신청 후 7일 후 DB에서 영구 삭제.
 *
 * - 닉네임 재사용 정책
 * - - 악의적 선점을 방지하기 위해 24시간의 유예기간 이후 타 사용자가 해당 닉네임으로 가입 가능.
 */
@Entity
@Table(name = "user", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_nickname_deleted_at",
                columnNames = {"nickname","deletedAt"}
        )
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseMutableTimeEntity {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 50, nullable = false)
    private String nickname;

    @Column(nullable = false)
    private int point = 0;

    @OneToMany(mappedBy = "author")
    private List<Question> writtenQuestionList = new ArrayList<>();

    @OneToMany(mappedBy = "author")
    private List<Answer> writtenAnswerList = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<UserInterest> userInterestList = new ArrayList<>();

    @Builder
    public User(String email,String password,String nickname){
        this.id = UuidUtil.createUuidV7();
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }
}
