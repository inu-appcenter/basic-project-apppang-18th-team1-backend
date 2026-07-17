package com.team1.appang.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

//상품의 브랜드 정보를 담는 엔티티
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Brand {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    //브랜드명은 하나만 존재해야 하므로 unique 지정
    @Column(nullable = false, unique = true)
    private String name;

    private String logoUrl;

    @Builder
    public Brand(String name, String logoUrl) {
        this.name = name;
        this.logoUrl = logoUrl;
    }
}