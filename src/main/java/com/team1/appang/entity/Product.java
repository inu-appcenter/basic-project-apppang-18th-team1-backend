package com.team1.appang.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private int discountRate;
    private String unitPriceText;

    @Lob
    private String detailImages; //상세 이미지, 화면을 아래로 내렸을 때 뜨는 상세 이미지 (JSON 배열 문자열)

    @Lob
    private String subImages; //추가: 상단 메인 이미지 외 이미지들 (JSON 배열 문자열)

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int originPrice;

    @Column(nullable = false)
    private int salePrice;

    @Column(nullable = false)
    private String mainImageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    //brand(Brand 엔티티) 연관관계로 교체
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @Builder
    public Product(int discountRate, String unitPriceText, String detailImages, String subImages,
                   String name, int originPrice, int salePrice, String mainImageUrl,
                   Category category, Brand brand) {
        this.discountRate = discountRate;
        this.unitPriceText = unitPriceText;
        this.detailImages = detailImages;
        this.subImages = subImages;
        this.name = name;
        this.originPrice = originPrice;
        this.salePrice = salePrice;
        this.mainImageUrl = mainImageUrl;
        this.category = category;
        this.brand = brand;
    }
}