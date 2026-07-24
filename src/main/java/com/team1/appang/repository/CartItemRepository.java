package com.team1.appang.repository;

import com.team1.appang.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

//장바구니 레포지토리
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    //회원 id를 기준으로 지우는 옵션
    void deleteByMemberId(Long memberId);

    //같은 회원이 같은 옵션의 상품을 담았는지 확인
    Optional<CartItem> findByMemberIdAndProductOptionId(Long memberId, Long productOptionId);



    //결제 금액 요약 계산을 위한 장바구니 전체 조회. 회원 아이디를 조회한다.
    //회원 한명이 여러개의 장바구니를 담을 수 있으므로 Optional이 아닌 List로 처리
    //Optional은 반환값이 0 또는 1이길 기대하기에 2개 이상일시 오류 발생
    //N+1문제를 해결하기 위해 카트 아이템을 가져올 때 연결된 옵션과 상품 정보도 가져오게 쿼리 설정
    @Query("select c from CartItem c " +
            "join fetch c.productOption po " +
            "join fetch po.product " +
            "where c.member.id = :memberId")
    List<CartItem> findByMemberIdWithProductInfo(long memberId);
}
