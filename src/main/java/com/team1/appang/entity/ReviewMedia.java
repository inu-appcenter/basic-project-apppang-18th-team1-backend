package com.team1.appang.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewMedia {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private String mediaUrl;

    @Column(nullable = false)
    private String mediaType;

    private int duration;

    @ManyToOne(fetch = FetchType.LAZY) // 일대다 관계이므로
    @JoinColumn(name = "product_review_id", nullable = false)
    private ProductReview productReview;

    @Builder
    public ReviewMedia(String mediaUrl, String mediaType, int duration,
                       ProductReview productReview){
        this.mediaType = mediaType;
        this.mediaUrl = mediaUrl;
        this.duration = duration;
        this.productReview = productReview;
    }
}
