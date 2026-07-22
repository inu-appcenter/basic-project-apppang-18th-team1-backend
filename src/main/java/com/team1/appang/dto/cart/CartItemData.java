package com.team1.appang.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Getter;


//장바구니에 담긴 상품 정보를 담는 DTO
@Getter
@AllArgsConstructor
public class CartItemData {
    private Long cartItemId;
    private Long productId;
    private Long optionId;
    private int quantity;
    private int maxQuantity;
    private CartSummary summary;
}