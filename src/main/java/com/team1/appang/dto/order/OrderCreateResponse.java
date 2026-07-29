package com.team1.appang.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;

public record OrderCreateResponse(
        @Schema(description = "결과 메시지", example = "주문이 완료되었습니다.")
        String message,
        @Schema(description = "생성된 주문 정보")
        OrderCreateData data
) {
}
