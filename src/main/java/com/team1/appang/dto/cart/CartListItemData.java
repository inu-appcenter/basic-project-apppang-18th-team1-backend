package com.team1.appang.dto.cart;

import io.swagger.v3.oas.annotations.media.Schema;

public record CartListItemData(
        @Schema(description = "장바구니 아이템 id", example = "1")
        Long cartItemId,
        @Schema(description = "상품 id", example = "1")
        Long productId,
        @Schema(description = "상품명", example = "무선 이어폰")
        String productName,
        @Schema(description = "썸네일 이미지 URL")
        String thumbnailUrl,
        @Schema(description = "브랜드명", example = "애플")
        String brandName,
        @Schema(description = "옵션 표시 텍스트", example = "색상 화이트+그레이")
        String optionText,
        @Schema(description = "예상 도착일", example = "2026-07-28")
        String estimatedArrivalDate,
        @Schema(description = "담긴 수량", example = "2")
        int quantity,
        @Schema(description = "최대 구매 가능 수량", example = "10")
        int maxQuantity,
        @Schema(description = "선택 여부", example = "true")
        boolean isSelected,
        @Schema(description = "가격 정보")
        CartItemPrice price
) {
}