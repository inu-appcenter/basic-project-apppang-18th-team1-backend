package com.team1.appang.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record OrderListResponse(
        @Schema(description = "결과 메시지", example = "주문 목록을 조회했습니다.")
        String message,
        @Schema(description = "주문 목록")
        List<OrderListItemData> data
) {
}
