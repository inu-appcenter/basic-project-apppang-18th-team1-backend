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

    //영문/한글 등 검색용 별칭. 여러 개면 쉼표로 구분해 저장 (예: "apple, 애플")
    private String alias;

    @Builder
    public Brand(String name, String logoUrl, String alias) {
        this.name = name;
        this.logoUrl = logoUrl;
        this.alias = alias;
    }
}