package com.team1.appang.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"member_id","product_review_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewHelpful {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_review_id",nullable = false)
    private ProductReview productReview;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id",nullable = false)
    private Member member;

    @Builder
    public ReviewHelpful(Member member, ProductReview productReview) {
        this.member =member;
        this.productReview =productReview;
    }
}
