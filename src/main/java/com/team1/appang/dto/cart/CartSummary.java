package com.team1.appang.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Getter;

//최종 결제금액 요약 정보를 담는 DTO
public record CartSummary (
    int totalOriginPrice,
    int totalInstantDiscount,
    int totalCouponDiscount,
    int totalShippingFee,
    int totalPaymentAmount
){}