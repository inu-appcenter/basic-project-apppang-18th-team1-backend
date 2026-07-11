package com.team1.appang.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductReview {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private int rating;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int helpfulCount;


    @ManyToOne(fetch = FetchType.LAZY) // 일대다 관계이므로
    @JoinColumn(name = "member_id")
    private Member member;


    @ManyToOne(fetch = FetchType.LAZY) // 일대다 관계이므로
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Builder
    public ProductReview(int rating, String content, int helpfulCount,
                         Member member,Product product){
        this.rating = rating;
        this.content = content;
        this.helpfulCount = helpfulCount;
        this.member = member;
        this.product = product;
    }
}
