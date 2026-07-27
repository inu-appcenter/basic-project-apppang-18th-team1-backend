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
        @Schema(description = "현재 로그인한 사용자의 도움돼요 여부 (비로그인 시 항상 false)", example = "false")
        boolean isHelpful,
        String thumbnailUrl,
        LocalDateTime createdAt

        ) {
    public static ReviewSummaryResponse from(ProductReview review, String thumbnailUrl, boolean isHelpful){
        return new ReviewSummaryResponse(
                review.getId(),
                review.getMember().getNickname(),
                review.getRating(),
                review.getContent(),
                review.getHelpfulCount(),
                isHelpful,
                thumbnailUrl,
                review.getCreatedAt()
                );
    }

}
