package com.team1.appang.dto.cart;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record ShippingGroupData (
        @Schema(description = "배송 그룹 id", example = "1")
        int groupId,
        @Schema(description = "배송 뱃지 텍스트", example = "로켓배송")
        String shippingBadge,
        @Schema(description = "해당 그룹에 속한 장바구니 아이템 목록")
        List<CartListItemData> items
){
}