package com.team1.appang.dto.search;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "상품 검색 응답")
public record SearchResponse (
        @Schema(description = "응답 메시지", example = "검색 결과 조회가 완료되었습니다.")
        String message,
        @Schema(description = "검색결과 데이터")
        SearchResultData data
){
}
