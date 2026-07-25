package com.team1.appang.dto.cart;

import io.swagger.v3.oas.annotations.media.Schema;

public record CartItemPrice (
        @Schema(description = "정가", example = "50000")
        int originalPrice,
        @Schema(description = "와우쿠폰 할인율(%)", example = "10")
        int wowCouponDiscountRate,
        @Schema(description = "판매가", example = "45000")
        int salePrice
){
}