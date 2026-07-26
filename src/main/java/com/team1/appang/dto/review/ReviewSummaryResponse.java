package com.team1.appang.dto.review;

import com.team1.appang.entity.ProductReview;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ReviewSummaryResponse(
        @Schema(description = "리뷰 id", example = "1")
        Long reviewId,
        String nickname,
        int rating,
        String content,
        int helpfulCount,
        String thumbnailUrl,
        LocalDateTime createdAt

        ) {
    public static ReviewSummaryResponse from(ProductReview review, String thumbnailUrl){
        return new ReviewSummaryResponse(
                review.getId(),
                review.getMember().getNickname(),
                review.getRating(),
                review.getContent(),
                review.getHelpfulCount(),
                thumbnailUrl,
                review.getCreatedAt()
                );
    }

}
