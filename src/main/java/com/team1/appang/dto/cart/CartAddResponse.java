package com.team1.appang.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Getter;


//프론트에 응답하는 DTO. 장바구니 아이템은 data에 담겨서 반환된다
public record CartAddResponse (
    String message,
    CartItemData data
){}