package com.team1.appang.dto.cart;

import io.swagger.v3.oas.annotations.media.Schema;

public record CartDeleteSummary(
        @Schema(description = "삭제 후 전체 상품 금액", example = "45000")
        int totalProductPrice,
        @Schema(description = "삭제 후 전체 할인 금액", example = "5000")
        int totalDiscount,
        @Schema(description = "삭제 후 최종 결제 금액", example = "40000")
        int totalPaymentAmount
) {
}