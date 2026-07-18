package com.team1.appang.service.product;

import com.team1.appang.dto.product.ProductListResponse;
import com.team1.appang.dto.product.ProductSortType;
import com.team1.appang.entity.Product;
import com.team1.appang.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) //조회 전용이므로 읽기 전용 트랜잭션 사용
public class ProductService {
    private final ProductRepository productRepository;

    //상품 조회 로직
    public ProductListResponse getProducts(Long categoryId, ProductSortType sortType, int page, int size) {

        Page<Product> productPage;

        if (sortType == ProductSortType.RANKING) {
            //랭킹(찜 개수)은 쿼리 자체에 ORDER BY가 포함되어 있으므로 Sort 없는 Pageable을 사용
            Pageable pageable = PageRequest.of(page, size);
            productPage = productRepository.findByCategoryOrderByWishlistCountDesc(categoryId, pageable);
        } else {
            //최신순/가격순은 Pageable의 Sort 기능으로 처리
            Sort sort = resolveSort(sortType);
            Pageable pageable = PageRequest.of(page, size, sort);
            productPage = productRepository.findByCategory(categoryId, pageable);
        }

        return ProductListResponse.from(productPage);
    }
    //정렬기준 Enum를 실제 JPA Sort 객체로 변환하는 메서드
    //RANKING은 예외처리를 함
    private Sort resolveSort(ProductSortType sortType) {
        return switch (sortType) {
            case LATEST -> Sort.by(Sort.Direction.DESC, "id");
            case PRICE_LOW -> Sort.by(Sort.Direction.ASC, "salePrice");
            case PRICE_HIGH -> Sort.by(Sort.Direction.DESC, "salePrice");
            case RANKING -> throw new IllegalStateException("RANKING은 별도 쿼리에서 처리됩니다."); //도달하지 않음
        };
    }
}
