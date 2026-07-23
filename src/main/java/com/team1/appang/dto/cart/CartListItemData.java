package com.team1.appang.dto.cart;

public record CartListItemData(
        Long cartItemId,
        Long productId,
        String productName,
        String thumbnailUrl,
        String brandName,
        String optionText,
        String estimatedArrivalDate,
        int quantity,
        int maxQuantity,
        boolean isSelected,
        CartItemPrice price
) {
}
