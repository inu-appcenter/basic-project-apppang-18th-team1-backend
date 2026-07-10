package com.team1.appang.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PurchaseItem {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private String deliveryBadgeType;

    @Column(nullable = false)
    private LocalDate estimatedDeliveryDate; //data 타입

    @Column(nullable = false)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY) // 일대다 관계이므로
    @JoinColumn(name = "purchase_id",nullable = false)
    private Purchase purchase;

    @ManyToOne(fetch = FetchType.LAZY) // 일대다 관계이므로
    @JoinColumn(name = "product_option_id",nullable = false)
    private ProductOption productOption;

    @Builder
    public PurchaseItem(int quantity, int price, String deliveryBadgeType,
                        LocalDate estimatedDeliveryDate, String status,
                        Purchase purchase, ProductOption productOption){
        this.quantity = quantity;
        this.price = price;
        this.deliveryBadgeType =deliveryBadgeType;
        this.estimatedDeliveryDate =estimatedDeliveryDate;
        this.status = status;
        this.purchase = purchase;
        this.productOption = productOption;

    }
}
