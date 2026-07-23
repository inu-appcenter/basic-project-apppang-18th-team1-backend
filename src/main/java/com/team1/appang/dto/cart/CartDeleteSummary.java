package com.team1.appang.dto.cart;

public record CartDeleteSummary(
        int totalProductPrice,
        int totalDiscount,
        int totalPaymentAmount
) {
}