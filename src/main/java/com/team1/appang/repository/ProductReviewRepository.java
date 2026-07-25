package com.team1.appang.repository;

import com.team1.appang.entity.ProductReview;
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
}