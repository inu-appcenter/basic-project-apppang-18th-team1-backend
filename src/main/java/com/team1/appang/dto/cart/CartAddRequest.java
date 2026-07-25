package com.team1.appang.dto.cart;

import io.swagger.v3.oas.annotations.media.Schema;


public record CartAddRequest (
        @Schema(description = "담을 상품 id", example = "1")
        int productId,
        @Schema(description = "담을 상품 옵션 id", example = "1")
        int optionId,
        @Schema(description = "담을 수량", example = "2")
        int quantity
){}