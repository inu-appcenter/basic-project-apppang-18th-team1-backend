package com.team1.appang.repository;

import com.team1.appang.entity.ProductReview;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {


    //평점평균과 리뷰수를 한번에 가져오는 쿼리문.
    //쿼리 메서드로 작성시 이름이 과다하게 길어져 직접 쿼리문 작성함
    @Query("select r.product.id, avg(r.rating), count(r) " +
            "from ProductReview r " +
            "where r.product.id in :productIds " +
            "group by r.product.id")
    List<Object[]> findRatingSummary(@Param("productIds") List<Long> productIds);

    //자동완성용 쿼리. 입력값으로 시작하는 상품명만 조회
    //같은 이름의 상품이 여러 개 있을 수 있으므로 DISTINCT로 중복 제거
    //Pageable로 최대 개수(10개)를 제한함
    @Query("SELECT DISTINCT p.name FROM Product p WHERE p.name LIKE :keyword%")
    List<String> findNamesStartingWith(@Param("keyword") String keyword, Pageable pageable);
}