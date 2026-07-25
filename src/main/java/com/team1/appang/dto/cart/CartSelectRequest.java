package com.team1.appang.dto.cart;

import io.swagger.v3.oas.annotations.media.Schema;

public record CartSelectRequest(
        @Schema(description = "변경할 선택 상태", example = "true")
        boolean isSelected
) {
}