package com.team1.appang.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;

//주문에 담긴 상품 하나를 표현하는 DTO. 주문 생성/목록 조회 응답에서 공통으로 사용
public record OrderItemData(
        @Schema(description = "상품 id", example = "1")
        Long productId,
        @Schema(description = "상품명", example = "무선 이어폰")
        String productName,
        @Schema(description = "썸네일 이미지 URL")
        String thumbnailUrl,
        @Schema(description = "브랜드명", example = "애플")
        String brandName,
        @Schema(description = "옵션 표시 텍스트", example = "색상 화이트+그레이")
        String optionText,
        @Schema(description = "주문 수량", example = "2")
        int quantity,
        @Schema(description = "주문 시점 단가(옵션 추가금 포함)", example = "45000")
        int price
) {
}
