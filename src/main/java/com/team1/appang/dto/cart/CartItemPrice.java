package com.team1.appang.dto.cart;

public record CartItemPrice (
        int originalPrice,
        int wowCouponDiscountRate,
        int salePrice
){
}
