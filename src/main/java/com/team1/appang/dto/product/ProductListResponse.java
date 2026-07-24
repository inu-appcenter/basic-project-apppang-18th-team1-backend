package com.team1.appang.dto.product;

import com.team1.appang.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;
import java.util.List;

public record ProductListResponse(
        @Schema(description = "상품 목록")
        List<ProductSummaryResponse> products,
        @Schema(description = "현재 페이지 (0부터 시작)", example = "0")
        int page,
        @Schema(description = "페이지 크기", example = "10")
        int size,
        @Schema(description = "전체 상품 수", example = "128")
        long totalElements,
        @Schema(description = "전체 페이지 수", example = "13")
        int totalPages
) {
    public static ProductListResponse from(Page<Product> productPage) {
        List<ProductSummaryResponse> products = productPage.getContent().stream()
                .map(ProductSummaryResponse::from)
                .toList();

        return new ProductListResponse(
                products,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages()
        );
    }
}