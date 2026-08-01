package com.team1.appang.controller.review;

import com.team1.appang.dto.MessageResponse;
import com.team1.appang.dto.review.ReviewCreateRequest;
import com.team1.appang.dto.review.ReviewCreateResponse;
import com.team1.appang.dto.review.ReviewHelpfulToggleResponse;
import com.team1.appang.dto.review.ReviewListResponse;
import com.team1.appang.dto.review.ReviewOwnershipResponse;
import com.team1.appang.dto.review.ReviewUpdateRequest;
import com.team1.appang.dto.review.ReviewUpdateResponse;
import com.team1.appang.exception.MemberNotFoundException;
import com.team1.appang.exception.ProductNotFoundException;
import com.team1.appang.exception.ReviewAccessDeniedException;
import com.team1.appang.exception.ReviewNotFoundException;
import com.team1.appang.exception.ReviewPurchaseRequiredException;
import com.team1.appang.service.auth.AuthService;
import com.team1.appang.service.review.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Review", description = "리뷰 작성 / 조회 관련 API")
@RestController
@RequestMapping("/api/products/{productId}/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    private final AuthService authService;

    @Operation(summary = "리뷰 작성", description = "상품에 대한 리뷰를 작성합니다. 로그인이 필요합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "작성 성공",
                    content = @Content(schema = @Schema(implementation = ReviewCreateResponse.class))),
            @ApiResponse(responseCode = "400", description = "요청 값이 유효하지 않음",
                    content = @Content(examples = @ExampleObject(value = """
                {
                  "message": "리뷰 내용을 작성해주세요."
                }
                """))),
            @ApiResponse(responseCode = "401", description = "로그인이 필요함",
                    content = @Content(examples = @ExampleObject(value = """
                {
                  "message": "로그인이 필요합니다."
                }
                """))),
            @ApiResponse(responseCode = "403", description = "구매 이력이 없음",
                    content = @Content(examples = @ExampleObject(value = """
                {
                  "message": "ReviewPurchaseRequiredException.message"
                }
                """))),
            @ApiResponse(responseCode = "404", description = "상품 또는 회원을 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = """
                {
                  "message": "ProductNotFoundException.message 또는 MemberNotFoundException.message"
                }
                """)))
    })
    @PostMapping
    public ResponseEntity<?> createReview(
            @Parameter(description = "리뷰를 작성할 상품 id", example = "1")
            @PathVariable Long productId,
            @Valid @RequestBody ReviewCreateRequest request,
            BindingResult bindingResult
            ){
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            String errorMessage = fieldError != null ? fieldError.getDefaultMessage() : "잘못된 요청입니다.";
            return ResponseEntity.badRequest().body(new MessageResponse(errorMessage));
        }
        Long memberId = authService.getCurrentMemberId();
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("로그인이 필요합니다."));
        }
        try {
            ReviewCreateResponse response = reviewService.createReview(productId, memberId, request);
            return ResponseEntity.ok(response);
        } catch (ProductNotFoundException | MemberNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse(e.getMessage()));
        } catch (ReviewPurchaseRequiredException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse(e.getMessage()));
        }
    }
    @Operation(summary = "리뷰 목록 조회", description = "리뷰 목록을 최신순으로 페이지네이션하여 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
            content =  @Content(schema = @Schema(implementation = ReviewListResponse.class))),
            @ApiResponse(responseCode = "400", description = "page 또는 size 값이 허용 범위 벗어남",
            content = @Content(examples = @ExampleObject(value = """
                    {
                        "message": "page는 1 이상이어야 합니다."
                    }"""))),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음",
            content = @Content(examples = @ExampleObject(value = """
                    
                    {
                        "message": "ProductNotFoundException.message"
                    }""")))
    })
    @GetMapping
    public ResponseEntity<?> getReviews(
            @PathVariable Long productId,
            @Parameter(description = "페이지 번호 (1부터 시작)", example = "1")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(defaultValue = "10") int size){
        if (page < 1) {
            return ResponseEntity.badRequest().body(new MessageResponse("page는 1 이상이어야 합니다."));
        }
        if (size <= 0 || size > 100) {
            return ResponseEntity.badRequest().body(new MessageResponse("size는 1~100 사이여야 합니다."));
        }
        try {
            Long memberId = authService.getCurrentMemberId();
            ReviewListResponse response = reviewService.getReviews(productId, memberId, page, size);
            return ResponseEntity.ok(response);
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(e.getMessage()));
        }

    }

    @Operation(summary = "리뷰 도움돼요 토글", description = "리뷰 도움돼요 상태를 토글합니다. 누르지 않았으면 추가하고, 이미 눌렀으면 취소합니다. 로그인이 필요합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토글 성공",
                    content = @Content(schema = @Schema(implementation = ReviewHelpfulToggleResponse.class))),
            @ApiResponse(responseCode = "401", description = "로그인이 필요함",
                    content = @Content(examples = @ExampleObject(value = """
                {
                  "message": "로그인이 필요합니다."
                }
                """))),
            @ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = """
                {
                  "message": "ReviewNotFoundException.message"
                }
                """)))
    })
    @PostMapping("/{reviewId}/helpful")
    public ResponseEntity<?> toggleHelpful(
            @Parameter(description = "리뷰가 속한 상품 id", example = "1")
            @PathVariable Long productId,
            @Parameter(description = "도움돼요를 토글할 리뷰 id", example = "1")
            @PathVariable Long reviewId) {
        Long memberId = authService.getCurrentMemberId();
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("로그인이 필요합니다."));
        }
        try {
            ReviewHelpfulToggleResponse response = reviewService.toggleHelpful(reviewId, memberId);
            return ResponseEntity.ok(response);
        } catch (ReviewNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(e.getMessage()));
        }
    }

    @Operation(summary = "리뷰 수정", description = "본인이 작성한 리뷰의 평점/내용을 수정합니다. 로그인이 필요합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = ReviewUpdateResponse.class))),
            @ApiResponse(responseCode = "401", description = "로그인이 필요함",
                    content = @Content(examples = @ExampleObject(value = """
                {
                  "message": "로그인이 필요합니다."
                }
                """))),
            @ApiResponse(responseCode = "403", description = "본인이 작성한 리뷰가 아님",
                    content = @Content(examples = @ExampleObject(value = """
                {
                  "message": "ReviewAccessDeniedException.message"
                }
                """))),
            @ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = """
                {
                  "message": "ReviewNotFoundException.message"
                }
                """)))
    })
    @PatchMapping("/{reviewId}")
    public ResponseEntity<?> updateReview(
            @Parameter(description = "리뷰가 속한 상품 id", example = "1")
            @PathVariable Long productId,
            @Parameter(description = "수정할 리뷰 id", example = "1")
            @PathVariable Long reviewId,
            @RequestBody ReviewUpdateRequest request) {
        Long memberId = authService.getCurrentMemberId();
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("로그인이 필요합니다."));
        }
        try {
            ReviewUpdateResponse response = reviewService.updateReview(reviewId, memberId, request);
            return ResponseEntity.ok(response);
        } catch (ReviewAccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse(e.getMessage()));
        } catch (ReviewNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(e.getMessage()));
        }
    }

    @Operation(summary = "리뷰 작성자 본인 확인", description = "수정하려는 리뷰가 본인이 작성한 리뷰인지 확인합니다. 로그인이 필요합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "확인 성공",
                    content = @Content(schema = @Schema(implementation = ReviewOwnershipResponse.class))),
            @ApiResponse(responseCode = "401", description = "로그인이 필요함",
                    content = @Content(examples = @ExampleObject(value = """
                {
                  "message": "로그인이 필요합니다."
                }
                """))),
            @ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = """
                {
                  "message": "ReviewNotFoundException.message"
                }
                """)))
    })
    @GetMapping("/{reviewId}/ownership")
    public ResponseEntity<?> checkOwnership(
            @Parameter(description = "리뷰가 속한 상품 id", example = "1")
            @PathVariable Long productId,
            @Parameter(description = "본인 여부를 확인할 리뷰 id", example = "1")
            @PathVariable Long reviewId) {
        Long memberId = authService.getCurrentMemberId();
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("로그인이 필요합니다."));
        }
        try {
            ReviewOwnershipResponse response = reviewService.checkOwnership(reviewId, memberId);
            return ResponseEntity.ok(response);
        } catch (ReviewNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("리뷰를 찾을 수 없습니다"));
        }
    }

}
