package com.team1.appang.repository;

import com.team1.appang.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

//주문 레포지토리
public interface OrderRepository extends JpaRepository<Order, Long> {

    //회원의 주문 목록 조회. 최신 주문이 먼저 보이도록 정렬
    //N+1 문제를 막기 위해 아이템과 옵션, 상품 정보까지 함께 조회
    @Query("select distinct o from Order o " +
            "join fetch o.items i " +
            "join fetch i.productOption po " +
            "join fetch po.product " +
            "where o.member.id = :memberId " +
            "order by o.createdAt desc")
    List<Order> findByMemberIdWithItems(Long memberId);

    //해당 회원이 이 상품을 구매한 이력이 있는지 확인 (리뷰 작성 자격 검증용)
    //취소된 주문은 구매로 인정하지 않음
    @Query("SELECT COUNT(o) > 0 FROM Order o JOIN o.items i JOIN i.productOption po " +
            "WHERE o.member.id = :memberId AND po.product.id = :productId " +
            "AND o.orderStatus <> com.team1.appang.entity.OrderStatus.CANCELLED")
    boolean existsPurchaseByMemberAndProduct(@Param("memberId") Long memberId, @Param("productId") Long productId);
}
