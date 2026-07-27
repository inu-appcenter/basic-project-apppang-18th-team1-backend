package com.team1.appang.dto.search;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "검색 결과 응답 데이터")
public record SearchResultData(
        @Schema(description = "검색에 사용된 키워드", example = "망고")
        String keyword,
        @Schema(description = "검색된 상품 목록")
        List<SearchProductResponse> products,
        @Schema(description = "마지막 페이지 여부", example = "false")
        boolean isLastPage
) {
}
