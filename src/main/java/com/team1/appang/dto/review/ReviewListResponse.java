package com.team1.appang.dto.review;

import com.team1.appang.entity.ProductReview;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public record ReviewListResponse(
        @Schema(description = "리뷰 목록")
        List<ReviewSummaryResponse> reviews,
        @Schema(description = "현재 페이지(1부터 시작)", example = "1")
        int page,
        @Schema(description = "페이지 크기", example = "10")
        int size,
        @Schema(description = "전체 리뷰 수", example = "42")
        long totalElements,
        @Schema(description = "전체 페이지 수", example = "5")
        int totalPages
        ) {
    public static ReviewListResponse from(Page<ProductReview> reviewPage, Map<Long, String> thumbnailByReviewId) {
        List<ReviewSummaryResponse> reviews = reviewPage.getContent().stream()
                .map(review -> ReviewSummaryResponse.from(review, thumbnailByReviewId.get(review.getId())))
                .toList();
        return new ReviewListResponse(
                reviews,
                reviewPage.getNumber() + 1,
                reviewPage.getSize(),
                reviewPage.getTotalElements(),
                reviewPage.getTotalPages()
        );
    }
}
