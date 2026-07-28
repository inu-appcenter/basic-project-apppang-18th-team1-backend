package com.team1.appang.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;

public record ReviewUpdateRequest(
        @Schema(description = "평점", example = "5")
        int rating,
        @Schema(description = "리뷰 내용", example = "착용감이 좋아요")
        String content
) {
}
