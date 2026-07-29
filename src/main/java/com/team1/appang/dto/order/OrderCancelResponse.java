package com.team1.appang.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;

public record OrderCancelResponse(
        @Schema(description = "결과 메시지", example = "주문이 취소되었습니다.")
        String message,
        @Schema(description = "취소 결과 데이터")
        OrderCancelData data
) {
}
