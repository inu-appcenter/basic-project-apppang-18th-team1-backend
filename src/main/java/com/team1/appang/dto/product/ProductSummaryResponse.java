package com.team1.appang.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;

//상품 목록 조회에 필요한 DTO
//엔티티를 그대로 반환하지 않고 필요한 필드만 추려서 응답
public record ProductSummaryResponse(
        @Schema(description = "상품 id", example = "1")
        Long id,
        @Schema(description = "브랜드명", example = "애플")
        String brandName,
        @Schema(description = "상품명", example = "무선 이어폰")
        String name,
        @Schema(description = "정가", example = "50000")
        int originPrice,
        @Schema(description = "할인율(%)", example = "10")
        int discountRate,
        @Schema(description = "판매가", example = "45000")
        int salePrice,
        @Schema(description = "대표 이미지 URL")
        String mainImageUrl,
        @Schema(description = "단가 표시 텍스트", example = "100ml당 4,500원")
        String unitPriceText
) {
    public static ProductSummaryResponse from(com.team1.appang.entity.Product product) {
        return new ProductSummaryResponse(
                product.getId(),
                product.getBrand().getName(),
                product.getName(),
                product.getOriginPrice(),
                product.getDiscountRate(),
                product.getSalePrice(),
                product.getMainImageUrl(),
                product.getUnitPriceText()
        );
    }
}