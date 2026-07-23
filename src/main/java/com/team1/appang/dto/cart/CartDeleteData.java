package com.team1.appang.dto.cart;

public record CartDeleteData(
        Long cartItemId,
        CartDeleteSummary summary) {
}