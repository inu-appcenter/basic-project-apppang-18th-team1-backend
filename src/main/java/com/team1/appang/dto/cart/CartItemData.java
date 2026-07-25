package com.team1.appang.dto.cart;

import io.swagger.v3.oas.annotations.media.Schema;

//장바구니에 담긴 상품 정보를 담는 DTO
public record CartItemData (
        @Schema(description = "장바구니 아이템 id", example = "1")
        Long cartItemId,
        @Schema(description = "상품 id", example = "1")
        Long productId,
        @Schema(description = "상품 옵션 id", example = "1")
        Long optionId,
        @Schema(description = "담긴 수량", example = "2")
        int quantity,
        @Schema(description = "최대 구매 가능 수량", example = "10")
        int maxQuantity,
        @Schema(description = "결제 금액 요약")
        CartSummary summary
){}