package com.team1.appang.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;

public record OrderCancelData(
        @Schema(description = "취소된 주문 id", example = "1")
        Long orderId,
        @Schema(description = "변경된 주문 상태", example = "취소")
        String orderStatus
) {
}
