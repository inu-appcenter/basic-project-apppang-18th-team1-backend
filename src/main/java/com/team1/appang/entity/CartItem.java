package com.team1.appang.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private int quantity;
    private boolean isSelected;


    @ManyToOne(fetch = FetchType.LAZY) // 일대다 관계이므로
    @JoinColumn(name = "member_id ")
    private Member member;


    @ManyToOne(fetch = FetchType.LAZY) // 일대다 관계이므로
    @JoinColumn(name = "product_option_id", nullable = false)
    private ProductOption productOption;

    @Builder
    public CartItem(int quantity, boolean isSelected,
                    Member member, ProductOption productOption){
        this.quantity = quantity;
        this.isSelected= isSelected;
        this.member=member;
        this.productOption = productOption;
    }


}
