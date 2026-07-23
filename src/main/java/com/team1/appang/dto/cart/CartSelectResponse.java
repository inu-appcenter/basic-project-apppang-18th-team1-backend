package com.team1.appang.dto.cart;

public record CartSelectResponse(
        String message,
        Long cartItemId,
        boolean isSelected
) {
}
