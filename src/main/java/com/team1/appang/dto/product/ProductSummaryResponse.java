package com.team1.appang.dto.product;

//상품 목록 조회에 필요한 DTO
//엔티티를 그대로 반환하지 않고 필요한 필드만 추려서 응답
public record ProductSummaryResponse(
        Long id,
        String brandName,
        String name,
        int originPrice,
        int discountRate,
        int salePrice,
        String mainImageUrl,
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