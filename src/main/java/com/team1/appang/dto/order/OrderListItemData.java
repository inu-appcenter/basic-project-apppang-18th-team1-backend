package com.team1.appang.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

//주문 목록에 표시되는 주문 하나(=Order) 단위 데이터
public record OrderListItemData(
        @Schema(description = "주문 id", example = "1")
        Long orderId,
        @Schema(description = "주문 상태", example = "주문완료")
        String orderStatus,
        @Schema(description = "최종 결제 금액", example = "85000")
        int finalPaymentPrice,
        @Schema(description = "주문 일시")
        LocalDateTime createdAt,
        @Schema(description = "주문에 담긴 상품 목록")
        List<OrderItemData> items
) {
}
