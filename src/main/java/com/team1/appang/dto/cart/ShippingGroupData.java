package com.team1.appang.dto.cart;

import java.util.List;

public record ShippingGroupData (
        int groupId,
        String shippingBadge,
        List<CartListItemData> items
){
}
