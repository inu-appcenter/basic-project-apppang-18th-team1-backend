package com.team1.appang.service.banner;

import com.team1.appang.dto.banner.MainBannerData;
import com.team1.appang.dto.banner.MainBannerResponse;
import com.team1.appang.dto.product.ProductSummaryResponse;
import com.team1.appang.entity.Product;
import com.team1.appang.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) //조회 전용이므로 읽기 전용 트랜잭션 사용
public class MainBannerService {

    private final ProductRepository productRepository;

    //메인페이지 배너 조회 로직. 판매량(취소되지 않은 주문 기준) 상위 상품을 배너로 노출
    public MainBannerResponse getMainBanners(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Product> products = productRepository.findTopSellingProducts(pageable);

        List<MainBannerData> data = new ArrayList<>();
        for (int i = 0; i < products.size(); i++) {
            data.add(new MainBannerData(i + 1, ProductSummaryResponse.from(products.get(i))));
        }

        return new MainBannerResponse("메인 배너를 조회했습니다.", data);
    }
}
