package com.team1.appang.controller.product;

import com.team1.appang.dto.auth.MessageResponse;
import com.team1.appang.dto.product.ProductDetailResponse;
import com.team1.appang.dto.product.ProductListResponse;
import com.team1.appang.dto.product.ProductSortType;
import com.team1.appang.entity.Member;
import com.team1.appang.exception.ProductNotFoundException;
import com.team1.appang.repository.MemberRepository;
import com.team1.appang.service.product.ProductDetailService;
import com.team1.appang.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    //서비스 연결
    private final ProductService productService;
    private final ProductDetailService productDetailService; //추가: 상세 조회 서비스
    private final MemberRepository memberRepository; //추가: 로그인한 회원의 email로 memberId를 조회하기 위함

    //상품 목록 조회 API
    //전체 조회 시 카테고리는 null이 됨
    //기본 sort는 latest
    @GetMapping
    public ResponseEntity<?> getProducts(
            @RequestParam(required = false) Long category,
            @RequestParam(required = false, defaultValue = "latest") String sort,
            @RequestParam(defaultValue = "0") int page, //기본값은 0
            @RequestParam(defaultValue = "10") int size //기본값은 10
    ){
        //page나 size에 이상한 값(1000000000000000000같이 DB에 부담을 주는 값)이 들어왔을때 대비
        //page, size값을 검증하고 문제가 있다면 400에러 반환
        if(page < 0){
            return ResponseEntity.badRequest().body(new MessageResponse("page는 0 이상이어야 합니다."));
        }
        //허용 범위를 1이상 100이하를 기준으로 잡음
        if (size <= 0||size>100) {
            return ResponseEntity.badRequest().body(new MessageResponse("size는 1~100 사이여야 합니다."));
        }
        ProductSortType sortType = ProductSortType.from(sort);
        ProductListResponse response = productService.getProducts(category, sortType, page, size);
        return ResponseEntity.ok(response);
    }

    //상품 상세 조회 API
    //로그인 상태면 isWishlist를 실제 값으로, 비로그인이면 false로 응답
    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductDetail(@PathVariable Long productId) {
        try {
            Long memberId = getCurrentMemberId(); //로그인 안 했으면 null 반환
            ProductDetailResponse response = productDetailService.getProductDetail(productId, memberId);
            return ResponseEntity.ok(response);
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(e.getMessage()));
        }
    }

    //SecurityContext에서 현재 로그인한 회원의 id를 꺼내는 헬퍼 메서드
    //JwtFilter가 인증 정보를 세팅해뒀다면 이메일을 꺼내 memberId로 변환, 없으면 null
    private Long getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        String email = (String) authentication.getPrincipal();
        return memberRepository.findByEmail(email)
                .map(Member::getId)
                .orElse(null);
    }
}