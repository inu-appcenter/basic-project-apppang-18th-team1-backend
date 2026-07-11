package com.team1.appang.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Banner {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private String imageUrl;

    private String linkUrl;
    private int sortOrder; //Not Null로 할지 상의 필요

    @Builder
    public Banner(String imageUrl, String linkUrl, int sortOrder){
        this.imageUrl =imageUrl;
        this.linkUrl =linkUrl;
        this.sortOrder = sortOrder;
    }
}
