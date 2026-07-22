package com.team1.appang.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Getter;

//최종 결제금액 요약 정보를 담는 DTO
@Getter
@AllArgsConstructor
public class CartSummary {
    private int totalOriginPrice;
    private int totalInstantDiscount;
    private int totalCouponDiscount;
    private int totalShippingFee;
    private int totalPaymentAmount;
}