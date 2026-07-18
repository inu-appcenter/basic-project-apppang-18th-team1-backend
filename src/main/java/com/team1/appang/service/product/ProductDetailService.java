package com.team1.appang.service.product;

import com.team1.appang.dto.product.ProductDetailResponse;
import com.team1.appang.entity.Product;
import com.team1.appang.entity.ProductOption;
import com.team1.appang.exception.ProductNotFoundException;
import com.team1.appang.repository.ProductOptionRepository;
import com.team1.appang.repository.ProductRepository;
import com.team1.appang.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//상품 상세 보기 서비스
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductDetailService {
    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final WishlistRepository wishlistRepository;

    //memberId는 비로그인 시 null로 전달됨
    public ProductDetailResponse getProductDetail(Long productId, Long memberId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("존재하지 않는 상품입니다."));

        List<ProductOption> options = productOptionRepository.findByProductId(productId);

        //비로그인(memberId == null)이면 무조건 false, 로그인 상태면 실제로 찜했는지 조회
        boolean isWishlist = memberId != null &&
                wishlistRepository.existsByMemberIdAndProductId(memberId, productId);

        return ProductDetailResponse.from(product, options, isWishlist);
    }
}