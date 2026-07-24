package com.team1.appang.controller.product;

import com.team1.appang.dto.MessageResponse;
import com.team1.appang.dto.product.ProductDetailResponse;
import com.team1.appang.dto.product.ProductListResponse;
import com.team1.appang.dto.product.ProductSortType;
import com.team1.appang.dto.product.WishlistToggleResponse;
import com.team1.appang.exception.MemberNotFoundException;
import com.team1.appang.exception.ProductNotFoundException;
import com.team1.appang.service.auth.AuthService;
import com.team1.appang.service.product.ProductDetailService;
import com.team1.appang.service.product.ProductService;
import com.team1.appang.service.product.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Product", description = "상품 조회 / 찜하기 관련 API")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    //서비스 연결
    private final ProductService productService;
    private final ProductDetailService productDetailService; //상세 조회 서비스
    private final WishlistService wishlistService;
    private final AuthService authService; //로그인한 회원 id 조회는 AuthService에 위임

    //상품 목록 조회 API
    //전체 조회 시 카테고리는 null이 됨
    //기본 sort는 latest
    @Operation(summary = "상품 목록 조회", description = "카테고리, 정렬 기준, 페이지 정보를 이용해 상품 목록을 조회합니다. 카테고리를 생략하면 전체 상품을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ProductListResponse.class))),
            @ApiResponse(responseCode = "400", description = "page 또는 size 값이 허용 범위를 벗어남",
                    content = @Content(examples = @ExampleObject(value = """
                {
                  "message": "page는 0 이상이어야 합니다. 또는 size는 1~100 사이여야 합니다."
                }
                """)))
    })
    @GetMapping
    public ResponseEntity<?> getProducts(
            @Parameter(description = "카테고리 id (생략 시 전체 조회)", example = "1")
            @RequestParam(required = false) Long category,
            @Parameter(description = "정렬 기준: ranking, latest, priceLow, priceHigh 중 하나 (기본값 latest)", example = "latest")
            @RequestParam(required = false, defaultValue = "latest") String sort,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page, //기본값은 0
            @Parameter(description = "페이지 크기 (1~100)", example = "10")
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
    @Operation(summary = "상품 상세 조회", description = "상품 상세 정보를 조회합니다. 로그인 상태면 찜 여부를 함께 반환하고, 비로그인이면 isWishlist는 false로 고정됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ProductDetailResponse.class))),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = """
                {
                  "message": "ProductNotFoundException.message"
                }
                """)))
    })
    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductDetail(
            @Parameter(description = "조회할 상품 id", example = "1")
            @PathVariable Long productId) {
        try {
            Long memberId = authService.getCurrentMemberId(); //로그인 안 했으면 null 반환
            ProductDetailResponse response = productDetailService.getProductDetail(productId, memberId);
            return ResponseEntity.ok(response);
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(e.getMessage()));
        }
    }

    //찜하기 토글 API
    //로그인 상태에서만 호출 가능. 비로그인 시 401 반환
    @Operation(summary = "찜하기 토글", description = "상품 찜 상태를 토글합니다. 찜되어 있지 않으면 추가하고, 찜되어 있으면 삭제합니다. 로그인이 필요합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토글 성공",
                    content = @Content(schema = @Schema(implementation = WishlistToggleResponse.class))),
            @ApiResponse(responseCode = "401", description = "로그인이 필요함",
                    content = @Content(examples = @ExampleObject(value = """
                {
                  "message": "로그인이 필요합니다."
                }
                """))),
            @ApiResponse(responseCode = "404", description = "상품 또는 회원을 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = """
                {
                  "message": "ProductNotFoundException.message 또는 MemberNotFoundException.message"
                }
                """)))
    })
    @PostMapping("/{productId}/wishlist")
    public ResponseEntity<?> toggleWishlist(
            @Parameter(description = "찜할 상품 id", example = "1")
            @PathVariable Long productId) {
        Long memberId = authService.getCurrentMemberId();

        //로그인하지 않은 상태면 401로 막음
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("로그인이 필요합니다."));
        }

        try {
            WishlistToggleResponse response = wishlistService.toggle(memberId, productId);
            return ResponseEntity.ok(response);

        } catch (ProductNotFoundException | MemberNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(e.getMessage()));
        }
    }
}