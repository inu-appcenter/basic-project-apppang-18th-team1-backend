package com.team1.appang.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

//상품의 카테고리를 담는 엔티티
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "category")
public class Category {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    //카테고리는 하나만 존재해야하므로 umique 지정
    @Column(nullable = false, unique = true)
    private String name;

    private String iconImageUrl;

    //정렬순서를 nullable로 할지는 논의 필요
    private Long sortOrder;

    @Builder
    public Category(String name, String iconImageUrl, Long sortOrder) {
        this.name = name;
        this.iconImageUrl = iconImageUrl;
        this.sortOrder = sortOrder;
    }
}
