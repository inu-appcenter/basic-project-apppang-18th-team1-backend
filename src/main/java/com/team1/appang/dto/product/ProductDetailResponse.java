package com.team1.appang.dto.product;

import com.team1.appang.entity.Product;
import com.team1.appang.entity.ProductOption;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

import java.util.List;

//상품 상세 조회 응답 DTO
public record ProductDetailResponse(
        @Schema(description = "상품 id", example = "1")
        Long productId,
        @Schema(description = "상품 이미지 목록 (대표 이미지 + 서브 이미지)")
        List<String> productImages, //mainImageUrl + subImages
        @Schema(description = "현재 로그인한 회원의 찜 여부 (비로그인 시 false)", example = "false")
        boolean isWishlist,
        @Schema(description = "브랜드 정보")
        BrandResponse brand,
        @Schema(description = "상품명", example = "무선 이어폰")
        String productName,
        @Schema(description = "정가", example = "50000")
        int originalPrice,
        @Schema(description = "할인율(%)", example = "10")
        int discountRate,
        @Schema(description = "판매가", example = "45000")
        int salePrice,
        @Schema(description = "단가 표시 텍스트", example = "100ml당 4,500원")
        String unitPriceText,
        @Schema(description = "구매 가능한 옵션 목록")
        List<ProductOptionResponse> variants,
        @Schema(description = "상세 설명 이미지 목록")
        List<String> detailImages
) {
    //Brand 정보를 담는 중첩 DTO
    public record BrandResponse(
            @Schema(description = "브랜드 id", example = "1")
            Long brandId,
            @Schema(description = "브랜드명", example = "애플")
            String brandName,
            @Schema(description = "브랜드 로고 이미지 URL")
            String brandLogoUrl) {
        public static BrandResponse from(com.team1.appang.entity.Brand brand) {
            return new BrandResponse(brand.getId(), brand.getName(), brand.getLogoUrl());
        }
    }

    //옵션 하나를 담는 중첩 DTO
    public record ProductOptionResponse(
            @Schema(description = "옵션(변형) id", example = "1")
            Long variantId,
            @Schema(description = "옵션명 (옵션명 + 옵션값 조합)", example = "색상 화이트+그레이")
            String variantName,
            @Schema(description = "옵션 추가 금액", example = "0")
            int price,
            @Schema(description = "배송 타입 표시명", example = "로켓배송")
            String shippingType,
            @Schema(description = "절약 금액", example = "1000")
            int saveAmount,
            @Schema(description = "인기 옵션 여부", example = "true")
            boolean isPopular
    ) {
        public static ProductOptionResponse from(ProductOption option) {
            return new ProductOptionResponse(
                    option.getId(),
                    option.getOptionName() + " " + option.getOptionValue(), //예: "색상 화이트+그레이"
                    option.getAdditionalPrice(),
                    option.getShippingType() != null ? option.getShippingType().getDisplayName() : null,
                    option.getSaveAmount(),
                    option.isPopular()
            );
        }
    }

    public static ProductDetailResponse from(Product product, List<ProductOption> options, boolean isWishlist) {
        return new ProductDetailResponse(
                product.getId(),
                ImageUtils.parseImages(product.getMainImageUrl(), product.getSubImages()),
                isWishlist,
                BrandResponse.from(product.getBrand()),
                product.getName(),
                product.getOriginPrice(),
                product.getDiscountRate(),
                product.getSalePrice(),
                product.getUnitPriceText(),
                options.stream().map(ProductOptionResponse::from).toList(),
                ImageUtils.parseImageList(product.getDetailImages())
        );
    }
}