package com.team1.appang.dto.cart;

//장바구니 수정 API에서 사용되는 CartSummary와 필드 내용이 달라 분리함
public record CartListSummary (
        int totalProductPrice,
        int totalCouponDiscount,
        int totalPaymentAmount
){
}
