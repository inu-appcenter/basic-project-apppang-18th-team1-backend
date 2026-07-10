package com.team1.appang.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Purchase {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private int totalProductPrice;

    @Column(nullable = false)
    private int totalDiscountPrice;

    @Column(nullable = false)
    private int finalPaymentPrice;

    @ManyToOne(fetch = FetchType.LAZY) // 일대다 관계이므로
    @JoinColumn(name = "member_id ")
    private Member member;

    @Builder
    public Purchase(int totalDiscountPrice, int totalProductPrice,
                    int finalPaymentPrice, Member member){
        this.totalDiscountPrice = totalDiscountPrice;
        this.totalProductPrice = totalProductPrice;
        this.finalPaymentPrice = finalPaymentPrice;
        this.member = member;
    }
}
