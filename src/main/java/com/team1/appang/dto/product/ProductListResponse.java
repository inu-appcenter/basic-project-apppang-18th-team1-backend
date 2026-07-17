package com.team1.appang.dto.product;

import com.team1.appang.entity.Product;
import org.springframework.data.domain.Page;
import java.util.List;

public record ProductListResponse(
        List<ProductSummaryResponse> products,
        int page,
        int size,
        long totalElements,
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