package com.team1.appang.controller.product;

import com.team1.appang.dto.auth.MessageResponse;
import com.team1.appang.dto.product.ProductListResponse;
import com.team1.appang.dto.product.ProductSortType;
import com.team1.appang.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
     //서비스 연결
    private final ProductService productService;

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
}
