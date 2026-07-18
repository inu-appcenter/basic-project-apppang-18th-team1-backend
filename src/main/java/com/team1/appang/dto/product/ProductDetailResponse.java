package com.team1.appang.dto.product;

import com.team1.appang.entity.Product;
import com.team1.appang.entity.ProductOption;
import java.util.List;

import java.util.List;

//상품 상세 조회 응답 DTO
public record ProductDetailResponse(
        Long productId,
        List<String> productImages, //mainImageUrl + subImages
        boolean isWishlist,
        BrandResponse brand,
        String productName,
        int originalPrice,
        int discountRate,
        int salePrice,
        String unitPriceText,
        List<ProductOptionResponse> variants,
        List<String> detailImages
) {
    //Brand 정보를 담는 중첩 DTO
    public record BrandResponse(Long brandId, String brandName, String brandLogoUrl) {
        public static BrandResponse from(com.team1.appang.entity.Brand brand) {
            return new BrandResponse(brand.getId(), brand.getName(), brand.getLogoUrl());
        }
    }

    //옵션 하나를 담는 중첩 DTO
    public record ProductOptionResponse(
            Long variantId,
            String variantName,
            int price,
            String shippingType,
            int saveAmount,
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