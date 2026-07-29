package com.team1.appang.dto.banner;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record MainBannerResponse(
        @Schema(description = "결과 메시지", example = "메인 배너를 조회했습니다.")
        String message,
        @Schema(description = "메인 배너 목록 (판매량 내림차순)")
        List<MainBannerData> data
) {
}
