package com.team1.appang.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

//내 위시리스트 목록 조회 응답
public record WishlistResponse(
        @Schema(description = "결과 메시지", example = "위시리스트를 조회했습니다.")
        String message,
        @Schema(description = "위시리스트에 담긴 상품 목록")
        List<ProductSummaryResponse> data
) {
}
