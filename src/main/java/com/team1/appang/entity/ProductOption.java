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


    @ManyToOne(fetch = FetchType.LAZY) // 일대다 관계이므로
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Builder
    public ProductOption(String optionName, String optionValue, int additionalPrice,
                         int stockQuantity, boolean isSoldOut, Product product){
        this.optionName = optionName;
        this.optionValue = optionValue;
        this.additionalPrice = additionalPrice;
        this.stockQuantity = stockQuantity;
        this.isSoldOut = isSoldOut;
        this.product = product;
    }
}
