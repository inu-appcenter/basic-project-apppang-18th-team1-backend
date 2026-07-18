package com.team1.appang.repository;

import com.team1.appang.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    //회원 + 상품 조합으로 찜 여부 확인
    boolean existsByMemberIdAndProductId(Long memberId, Long productId);

    //실제 Wishlist row를 찾아야 삭제(취소)가 가능하므로 추가
    Optional<Wishlist> findByMemberIdAndProductId(Long memberId, Long productId);
}