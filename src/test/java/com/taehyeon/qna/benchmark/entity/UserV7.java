package com.taehyeon.qna.benchmark.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class UserV7 {
    @Id
    private UUID id;
    private String name;

    public UserV7(UUID id) {
        this.id = id;
        this.name = "v7-tester";
    }
}