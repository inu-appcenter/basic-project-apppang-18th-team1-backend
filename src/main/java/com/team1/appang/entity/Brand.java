package com.team1.appang.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Brand {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    //브랜드명은 하나만 존재
    @Column(nullable = false, unique = true)
    private String name;

    private String logoUrl;

    @Builder
    public Brand(String name, String logoUrl) {
        this.name = name;
        this.logoUrl = logoUrl;
    }
}