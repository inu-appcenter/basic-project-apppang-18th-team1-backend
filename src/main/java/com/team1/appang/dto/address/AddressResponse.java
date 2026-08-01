package com.team1.appang.dto.address;

import io.swagger.v3.oas.annotations.media.Schema;

//배송지 단건 결과를 반환하는 응답 (등록/수정/기본 배송지 지정)
public record AddressResponse(
        @Schema(description = "결과 메시지", example = "배송지가 등록되었습니다.")
        String message,
        @Schema(description = "배송지 정보")
        AddressData data
) {
}
