package com.team1.appang.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.w3c.dom.Text;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "product")
public class Product {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String brandName;
    private int discountRate;
    private String unitPriceText;; //단위당 가격

    @Lob
    private String detailImages; //상세 이미지

    //nullable 특징
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int originPrice;

    @Column(nullable = false)
    private int salePrice;

    @Column(nullable = false)
    private String mainImageUrl;

    //외래키. JPA 규칙에 맞춰 객체로 생성
    @ManyToOne(fetch = FetchType.LAZY) // 일대다 관계이므로
    @JoinColumn(name = "category_id")
    private Category category;

    @Builder
    public Product(String brandName, int discountRate, String unitPriceText, String detailImages,
                   String name, int originPrice, int salePrice, String mainImageUrl, Category category) {
        this.brandName = brandName;
        this.discountRate = discountRate;
        this.unitPriceText = unitPriceText;
        this.detailImages = detailImages;
        this.name = name;
        this.originPrice = originPrice;
        this.salePrice = salePrice;
        this.mainImageUrl = mainImageUrl;
        this.category = category;
    }
}
