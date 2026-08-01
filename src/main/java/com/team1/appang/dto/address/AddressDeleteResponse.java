package com.team1.appang.dto.address;

import io.swagger.v3.oas.annotations.media.Schema;

public record AddressDeleteResponse(
        @Schema(description = "결과 메시지", example = "배송지가 삭제되었습니다.")
        String message,
        @Schema(description = "삭제된 배송지 id", example = "1")
        Long addressId
) {
}
