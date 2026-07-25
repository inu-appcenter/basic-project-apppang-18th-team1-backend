package com.team1.appang.controller.search;

import com.team1.appang.dto.MessageResponse;
import com.team1.appang.dto.search.SearchResponse;
import com.team1.appang.dto.search.SearchResultData;
import com.team1.appang.service.search.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "검색", description = "상품 검색 관련 API")
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService; //서비스 연결

    @Operation(
            summary = "상품 검색",
            description = "키워드로 상품을 검색합니다. 정렬 기준(RANKING, LATEST, LOW_PRICE, HIGH_PRICE)과 페이지네이션을 지원합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검색 성공",
                    content = @Content(schema = @Schema(implementation = SearchResponse.class),
                            examples = @ExampleObject(value = """
                {
                  "message": "검색 결과 조회가 완료되었습니다.",
                  "data": {
                    "keyword": "유기농 바나나",
                    "products": [
                      {
                        "productId": 1001,
                        "thumbnailUrl": "https://example.com/banana.jpg",
                        "productName": "맛있는 유기농 바나나 1kg",
                        "originalPrice": 15000,
                        "discountRate": 20,
                        "salePrice": 12000,
                        "unitPriceText": "10g당 120원",
                        "rating": 4.5,
                        "reviewCount": 128
                      }
                    ],
                    "isLastPage": false
                  }
                }
                """))),
            @ApiResponse(responseCode = "400", description = "page 또는 size 값이 유효하지 않음",
                    content = @Content(examples = @ExampleObject(value = """
                {
                  "message": "page는 1 이상이어야 합니다."
                }
                """)))
    })
    @GetMapping
    public ResponseEntity<?> search(
            @Parameter(description = "검색 키워드", example = "유기농 바나나")
            @RequestParam String keyword,

            @Parameter(description = "정렬 기준 (RANKING: 찜 많은 순, LATEST: 최신순, LOW_PRICE: 낮은 가격순, HIGH_PRICE: 높은 가격순)", example = "RANKING")
            @RequestParam(defaultValue = "RANKING") String sort,

            @Parameter(description = "페이지 번호 (1부터 시작)", example = "1")
            @RequestParam(defaultValue = "1") int page,

            @Parameter(description = "한 페이지당 노출할 상품 수", example = "20")
            @RequestParam(defaultValue = "20") int size
    ) {
        //page는 1부터 시작하므로 1보다 작은 값이 오면 400 처리
        if (page < 1) {
            return ResponseEntity.badRequest().body(new MessageResponse("page는 1 이상이어야 합니다."));
        }
        if (size <= 0 || size > 100) {
            return ResponseEntity.badRequest().body(new MessageResponse("size는 1~100 사이여야 합니다."));
        }

        SearchResultData data = searchService.search(keyword, sort, page, size);
        return ResponseEntity.ok(new SearchResponse("검색 결과 조회가 완료되었습니다.", data));
    }
}