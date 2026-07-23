package com.team1.appang.dto.cart;

import java.util.List;

public record CartListData (
        List<ShippingGroupData> shippingGroups,
        CartListSummary summary
){
}