package com.team1.appang.dto.banner;

import com.team1.appang.dto.product.ProductSummaryResponse;
import io.swagger.v3.oas.annotations.media.Schema;

//메인페이지 배너 하나를 표현하는 DTO
public record MainBannerData(
        @Schema(description = "배너 순위 (1부터 시작)", example = "1")
        int rank,
        @Schema(description = "배너에 노출할 상품 정보")
        ProductSummaryResponse product
) {
}
