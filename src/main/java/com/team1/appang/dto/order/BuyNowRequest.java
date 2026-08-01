package com.team1.appang.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;

public record BuyNowRequest(
        @Schema(description = "구매할 상품 id", example = "1")
        int productId,
        @Schema(description = "구매할 상품 옵션 id", example = "1")
        int optionId,
        @Schema(description = "구매할 수량", example = "2")
        int quantity,
        @Schema(description = "사용할 배송지 id", example = "1")
        Long addressId
) {
}
