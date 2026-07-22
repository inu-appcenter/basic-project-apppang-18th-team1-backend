package com.team1.appang.dto.cart;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CartAddRequest {
    private int productId;
    private int optionId;
    private int quantity;
}
