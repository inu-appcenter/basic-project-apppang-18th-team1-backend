package com.team1.appang.dto.address;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record AddressListResponse(
        @Schema(description = "결과 메시지", example = "배송지 목록을 조회했습니다.")
        String message,
        @Schema(description = "배송지 목록")
        List<AddressData> data
) {
}
