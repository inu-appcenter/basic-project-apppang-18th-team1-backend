package com.team1.appang.dto.cart;

import io.swagger.v3.oas.annotations.media.Schema;

public record CartListResponse (
        @Schema(description = "결과 메시지", example = "장바구니 목록을 조회했습니다.")
        String message,
        @Schema(description = "장바구니 목록 데이터")
        CartListData data
){
}