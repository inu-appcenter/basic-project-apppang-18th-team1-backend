package com.team1.appang.dto.cart;

import io.swagger.v3.oas.annotations.media.Schema;

//프론트에 응답하는 DTO. 장바구니 아이템은 data에 담겨서 반환된다
public record CartAddResponse (
        @Schema(description = "결과 메시지", example = "장바구니에 담았습니다.")
        String message,
        @Schema(description = "장바구니에 담긴 상품 정보")
        CartItemData data
){}