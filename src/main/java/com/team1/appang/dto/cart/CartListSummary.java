package com.team1.appang.dto.cart;

import io.swagger.v3.oas.annotations.media.Schema;

//장바구니 수정 API에서 사용되는 CartSummary와 필드 내용이 달라 분리함
public record CartListSummary (
        @Schema(description = "전체 상품 금액", example = "90000")
        int totalProductPrice,
        @Schema(description = "전체 쿠폰 할인 금액", example = "5000")
        int totalCouponDiscount,
        @Schema(description = "최종 결제 금액", example = "85000")
        int totalPaymentAmount
){
}