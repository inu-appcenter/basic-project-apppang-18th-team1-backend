package com.team1.appang.dto.cart;

import io.swagger.v3.oas.annotations.media.Schema;

public record CartDeleteResponse(
        @Schema(description = "결과 메시지", example = "상품이 삭제되었습니다.")
        String message,
        @Schema(description = "삭제 결과 데이터")
        CartDeleteData data) {
}