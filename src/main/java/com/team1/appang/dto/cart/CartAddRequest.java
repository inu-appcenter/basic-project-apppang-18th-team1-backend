package com.team1.appang.dto.cart;

import lombok.Getter;
import lombok.NoArgsConstructor;


public record CartAddRequest (
    int productId,
    int optionId,
    int quantity
){}
