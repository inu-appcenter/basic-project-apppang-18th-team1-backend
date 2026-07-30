package com.team1.appang.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;

public record ReviewOwnershipResponse(
        @Schema(description = "본인이 작성한 리뷰인지 여부", example = "true")
        boolean isOwner
) {
}
