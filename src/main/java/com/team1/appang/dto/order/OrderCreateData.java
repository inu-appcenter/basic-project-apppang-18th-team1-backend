package com.team1.appang.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record OrderCreateData(
        @Schema(description = "생성된 주문 id", example = "1")
        Long orderId,
        @Schema(description = "주문 상태", example = "주문완료")
        String orderStatus,
        @Schema(description = "전체 상품 정가 합", example = "90000")
        int totalProductPrice,
        @Schema(description = "전체 할인 금액", example = "5000")
        int totalDiscountPrice,
        @Schema(description = "최종 결제 금액", example = "85000")
        int finalPaymentPrice,
        @Schema(description = "주문에 담긴 상품 목록")
        List<OrderItemData> items
) {
}
