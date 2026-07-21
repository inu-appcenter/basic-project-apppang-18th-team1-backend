package com.team1.appang.repository;

import com.team1.appang.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

//장바구니 레포지토리
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    //같은 회원이 같은 옵션의 상품을 담았는지 확인
    Optional<CartItem> findByMemberIdAndProductOptionId(Long memberId, Long productOptionId);

    //결제 금액 요약 계산을 위한 장바구니 전체 조회. 회원 아이디를 조회한다.
    //회원 한명이 여러개의 장바구니를 담을 수 있으므로 Optional이 아닌 List로 처리
    //Optional은 반환값이 0 또는 1이길 기대하기에 2개 이상일시 오류 발생
    List<CartItem> findByMemberId(long memberId);
}
