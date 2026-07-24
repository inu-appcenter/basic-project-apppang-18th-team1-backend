package com.team1.appang.dto.cart;

import io.swagger.v3.oas.annotations.media.Schema;

public record CartDeleteData(
        @Schema(description = "삭제된 장바구니 아이템 id", example = "1")
        Long cartItemId,
        @Schema(description = "삭제 후 결제 금액 요약")
        CartDeleteSummary summary) {
}