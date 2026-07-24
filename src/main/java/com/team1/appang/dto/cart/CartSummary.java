package com.team1.appang.dto.cart;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

//최종 결제금액 요약 정보를 담는 DTO
public record CartSummary (
        @Schema(description = "전체 정가 합", example = "50000")
        int totalOriginPrice,
        @Schema(description = "즉시 할인 합", example = "5000")
        int totalInstantDiscount,
        @Schema(description = "쿠폰 할인 합", example = "0")
        int totalCouponDiscount,
        @Schema(description = "총 배송비", example = "0")
        int totalShippingFee,
        @Schema(description = "최종 결제 금액", example = "45000")
        int totalPaymentAmount
){}