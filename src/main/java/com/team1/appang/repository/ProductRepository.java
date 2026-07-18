package com.team1.appang.repository;

import com.team1.appang.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    //일반 정렬(최신순/가격순)용 쿼리. 카테고리 필터만 걸고, 실제 정렬은 Pageable의 Sort로 처리
    //categoryId가 null이면 전체 카테고리를 대상으로 조회
    @Query("SELECT p FROM Product p WHERE (:categoryId IS NULL OR p.category.id = :categoryId)")
    Page<Product> findByCategory(@Param("categoryId") Long categoryId, Pageable pageable);

    //랭킹(찜 개수) 정렬용 쿼리 Wishlist를 LEFT JOIN하여 상품별 찜 개수를 세고 내림차순 정렬
    //랭킹을 찜 개수로 정렬하는 쿼리는 임시로, 회의에서 정한뒤 확정지을 예정
    // 이 하나도 없는 상품도 목록에서 빠지지 않고 0개로 포함되어야 하므로 LEFT JOIN사용
    //GROUP BY를 쓰면 기본 count 쿼리가 부정확해지므로 countQuery를 별도로 명시
    @Query(
            value = "SELECT p FROM Product p LEFT JOIN Wishlist w ON w.product = p " +
                    "WHERE (:categoryId IS NULL OR p.category.id = :categoryId) " +
                    "GROUP BY p " +
                    "ORDER BY COUNT(w) DESC",
            countQuery = "SELECT COUNT(p) FROM Product p " +
                    "WHERE (:categoryId IS NULL OR p.category.id = :categoryId)"
    )
    Page<Product> findByCategoryOrderByWishlistCountDesc(
            @Param("categoryId") Long categoryId,
            Pageable pageable
    );
}