package com.team1.appang.service.search;

import com.team1.appang.dto.search.SearchProductResponse;
import com.team1.appang.dto.search.SearchResultData;
import com.team1.appang.dto.search.SearchSortType;
import com.team1.appang.entity.Product;
import com.team1.appang.entity.RecommendKeyword;
import com.team1.appang.repository.ProductRepository;
import com.team1.appang.repository.ProductReviewRepository;
import com.team1.appang.repository.RecommendKeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final ProductRepository productRepository;
    private final ProductReviewRepository productReviewRepository;
    private final RecommendKeywordRepository recommendKeywordRepository;

    //상품 검색 로직
    public SearchResultData search(String keyword, String sortParam, int page, int size){
        //문자열로 들어온 파라미터를 Enum로 변환
        //잘못된 값이 들어와도 SearchSortType.from 내부에서 기본값인 랭킹으로 처리해줌
        SearchSortType sortType = SearchSortType.from(sortParam);

        //page는 1부터 받으므로 Pageable에는 -1해서 넘김 (Pageable은 0부터 시작)
        Page<Product> productPage = getProductPage(keyword, sortType, page - 1, size);
        //Page 객체 안에서 실제 상품 목록만 꺼냄
        List<Product> products = productPage.getContent();

        //평점, 리뷰수 집계를 한번에 조회하여 N+1문제를 방지
        Map<Long, double[]> ratingMap = getRatingMap(products);

        //Product 엔티티 리스트를 하나씩 응답용 DTO로 변환
        //.stream().map().toList() : 리스트의 각 요소를 변환해 새 리스트를 만듦
        List<SearchProductResponse> productResponses = products.stream()
                .map(product -> toProductResponse(product, ratingMap))
                .toList();
        return new SearchResultData(keyword, productResponses, productPage.isLast());
    }

    //정렬기준에 따라 다른 쿼리를 호출하는 메서드
    //이때 page는 0에서부터 시작함
    private Page<Product> getProductPage(String keyword, SearchSortType sortType, int page, int size) {
        //RANKING은 찜 개수라는 별도의 계산된 값으로 정렬해야 하므로
        //일반적인 Sort 객체가 아닌 전용 메서드 사용
        if (sortType == SearchSortType.RANKING) {
            //Sort를 따로 안 넣는 이유: 정렬 순서가 쿼리 안에 이미 포함되어 있어서, 여기서 또 넣으면 오히려 충돌/무시됨
            Pageable pageable = PageRequest.of(page, size);
            return productRepository.searchByWishlist(keyword, pageable);
        }

        //RANKING외 다른 정렬은 Sort 사용
        Sort sort = switch(sortType){
            case LOW_PRICE -> Sort.by("salePrice").ascending();   //판매가 오름차순 (싼 것부터)
            case HIGH_PRICE -> Sort.by("salePrice").descending(); //판매가 내림차순 (비싼 것부터)
            case LATEST -> Sort.by("id").descending();            //id가 클수록 나중에 등록된 상품이므로, id 역순 = 최신순
            default -> Sort.unsorted();                           //여기 도달할 일은 없지만 방어 코드로 무정렬 처리
        };

        //페이지 번호 + 사이즈 + 정렬 기준을 하나로 묶어서 조회
        Pageable pageable = PageRequest.of(page, size, sort);
        return productRepository.searchByKeyword(keyword, pageable);
    }

    //평점, 리뷰수 집계 조회 메서드
    private Map<Long, double[]> getRatingMap(List<Product> products) {

        //조회할 상품들의 id만 뽑아서 리스트로 만듦
        List<Long> productIds = products.stream().map(Product::getId).toList();

        //검색 결과가 아예 없으면 굳이 DB에 쿼리 날릴 필요가 없으므로 바로 빈 Map 반환
        if (productIds.isEmpty()) {
            return new HashMap<>();
        }

        //위에서 모은 id들로 한 번에 평점 평균/리뷰 개수를 집계해서 가져옴
        //Object[] 형태로 오는 이유: 이 쿼리가 하나의 엔티티가 아니라 (상품id, 평균, 개수) 여러 값을 한 줄에 묶어서 반환하기 때문
        List<Object[]> rows = productReviewRepository.findRatingSummary(productIds);

        //조회 결과를 Map으로 변환해서 상품 id로 바로 찾아볼 수 있게 정리함
        Map<Long, double[]> map = new HashMap<>();
        for (Object[] row : rows) {
            Long productId = (Long) row[0];       //0번째 값 = 상품 id
            //리뷰가 하나도 없는 상품은 avg 결과가 null로 나올 수 있어서, null이면 0.0으로 대체
            double avgRating = row[1] == null ? 0.0 : (Double) row[1]; //1번째 값 = 평균 평점
            long reviewCount = (Long) row[2];      //2번째 값 = 리뷰 개수
            map.put(productId, new double[]{avgRating, reviewCount});
        }
        return map;
    }

    // 검색 응답 DTO 변환 메서드
    private SearchProductResponse toProductResponse(Product product, Map<Long, double[]> ratingMap) {

        //해당 상품의 평점 정보를 Map에서 꺼냄. 혹시 못 찾으면 [0.0, 0]으로 기본값 처리
        double[] rating = ratingMap.getOrDefault(product.getId(), new double[]{0.0, 0});

        return new SearchProductResponse(
                product.getId(),
                product.getMainImageUrl(),
                product.getName(),
                product.getOriginPrice(),
                product.getDiscountRate(),
                product.getSalePrice(),
                product.getUnitPriceText(),
                Math.round(rating[0] * 10) / 10.0, //예: 4.567 -> 45.67 -> 반올림 46 -> 4.6 (소수 첫째자리까지만 남김)
                (long) rating[1]
        );
    }

    //검색 페이지 초기 화면의 추천 검색어 조회 로직 (최대 10개)
    public List<String> getRecommendKeywords() {
        return recommendKeywordRepository
                //개수를 바꾼다면 여기서 조절하기
                .findAllByOrderBySortOrderAsc(PageRequest.of(0, 10))
                .stream()
                .map(RecommendKeyword::getKeyword)
                .toList();
    }

    //검색어 자동완성 로직
    //입력값으로 시작하는 상품명을 최대 10개까지 반환
    //검색어 추천과 개수를 맞췄으나 피그마 화면상에서는 11개이기에 개수 제안은 회의 필요
    public List<String> getAutocompleteSuggestions(String keyword) {
        //빈 문자열이면 DB에 쿼리 날릴 필요 없이 바로 빈 리스트 반환
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }
        //개수 수정은 여기서 고치면 됨
        return productRepository.findNamesStartingWith(keyword, PageRequest.of(0, 10));
    }
}