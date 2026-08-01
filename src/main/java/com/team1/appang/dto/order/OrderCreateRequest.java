package com.team1.appang.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;

public record OrderCreateRequest(
        @Schema(description = "사용할 배송지 id", example = "1")
        Long addressId
) {
}
