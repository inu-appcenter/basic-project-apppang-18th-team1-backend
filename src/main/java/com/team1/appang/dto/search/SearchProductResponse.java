package com.team1.appang.dto.search;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "검색 결과 상품 정보")
public record SearchProductResponse(
        @Schema(description = "상품 id", example = "1")
        Long productId,
        @Schema(description = "썸네일 이미지 URL")
        String thumbnailUrl,
        @Schema(description = "상품명", example = "바나나 우유")
        String productName,
        @Schema(description = "정가", example = "50000")
        int originalPrice,
        @Schema(description = "할인율(%)", example = "10")
        int discountRate,
        @Schema(description = "판매가", example = "45000")
        int salePrice,
        @Schema(description = "평균 평점 (리뷰가 없으면 0)", example = "3.5")
        double rating,
        @Schema(description = "리뷰 개수", example = "128")
        long reviewCount
) {
}
