package com.team1.appang.dto.cart;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record CartListData (
        @Schema(description = "배송 타입별로 그룹핑된 장바구니 목록")
        List<ShippingGroupData> shippingGroups,
        @Schema(description = "결제 금액 요약")
        CartListSummary summary
){
}