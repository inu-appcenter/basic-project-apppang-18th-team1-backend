package com.team1.appang.repository;

import com.team1.appang.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    //회원 + 상품 조합으로 찜 여부 확인
    boolean existsByMemberIdAndProductId(Long memberId, Long productId);

    //실제 Wishlist row를 찾아야 삭제(취소)가 가능하므로 추가
    Optional<Wishlist> findByMemberIdAndProductId(Long memberId, Long productId);

    //내 위시리스트 목록 조회 (상품, 브랜드 정보를 함께 조회하여 N+1 방지, 찜한 순서 최신순)
    @Query("select w from Wishlist w " +
            "join fetch w.product p " +
            "join fetch p.brand " +
            "where w.member.id = :memberId " +
            "order by w.createdAt desc")
    List<Wishlist> findByMemberIdWithProduct(Long memberId);
}