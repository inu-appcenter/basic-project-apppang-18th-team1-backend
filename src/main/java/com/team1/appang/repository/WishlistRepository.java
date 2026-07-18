package com.team1.appang.repository;

import com.team1.appang.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    //회원 + 상품 조합으로 찜 여부 확인
    boolean existsByMemberIdAndProductId(Long memberId, Long productId);
}