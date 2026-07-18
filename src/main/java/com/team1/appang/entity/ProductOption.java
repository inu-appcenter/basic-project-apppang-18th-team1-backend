package com.team1.appang.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductOption {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private String optionName;

    @Column(nullable = false)
    private String optionValue;

    @Column(nullable = false)
    private int additionalPrice;

    @Column(nullable = false)
    private int stockQuantity;

    private boolean isSoldOut;

    //Enum을 문자열 그대로 저장, 숫자는 순서 바뀔시 오류 가능성이 높음
    @Enumerated(EnumType.STRING)
    private ShippingType shippingType;

    //이 옵션 선택 시 절약되는 금액
    private int saveAmount;

    //인기 옵션 여부 (뱃지 표시용)
    private boolean isPopular;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Builder
    public ProductOption(String optionName, String optionValue, int additionalPrice,
                         int stockQuantity, boolean isSoldOut, ShippingType shippingType,
                         int saveAmount, boolean isPopular, Product product){
        this.optionName = optionName;
        this.optionValue = optionValue;
        this.additionalPrice = additionalPrice;
        this.stockQuantity = stockQuantity;
        this.isSoldOut = isSoldOut;
        this.shippingType = shippingType;
        this.saveAmount = saveAmount;
        this.isPopular = isPopular;
        this.product = product;
    }
}