package com.team1.appang.service.product;

import com.team1.appang.dto.product.WishlistToggleResponse;
import com.team1.appang.entity.Member;
import com.team1.appang.entity.Product;
import com.team1.appang.entity.Wishlist;
import com.team1.appang.exception.ProductNotFoundException;
import com.team1.appang.repository.MemberRepository;
import com.team1.appang.repository.ProductRepository;
import com.team1.appang.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

//찜하기 토글을 전담하는 서비스
@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;

    //DB에 추가/삭제가 일어나므로 @Transactional 필요
    @Transactional
    public WishlistToggleResponse toggle(Long memberId, Long productId) {
        //상품이 실제로 존재하는지 먼저 확인
        Product product = productRepository.findById(productId)
                .orElseThrow(ProductNotFoundException::new);

        //이미 찜한 상태인지 조회
        Optional<Wishlist> existing = wishlistRepository.findByMemberIdAndProductId(memberId, productId);

        if (existing.isPresent()) {
            //이미 찜한 상태 -> 삭제(취소)
            wishlistRepository.delete(existing.get());
            return WishlistToggleResponse.removed();
        } else {
            //아직 찜 안 한 상태 -> 추가
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(IllegalStateException::new); //인증된 회원이므로 사실상 발생 안 함

            Wishlist wishlist = Wishlist.builder()
                    .member(member)
                    .product(product)
                    .build();
            wishlistRepository.save(wishlist);
            return WishlistToggleResponse.added();
        }
    }
}