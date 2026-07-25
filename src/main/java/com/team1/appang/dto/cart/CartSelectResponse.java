package com.team1.appang.dto.cart;

import io.swagger.v3.oas.annotations.media.Schema;

public record CartSelectResponse(
        @Schema(description = "결과 메시지", example = "선택 상태가 변경되었습니다.")
        String message,
        @Schema(description = "장바구니 아이템 id", example = "1")
        Long cartItemId,
        @Schema(description = "변경된 선택 상태", example = "true")
        boolean isSelected
) {
}