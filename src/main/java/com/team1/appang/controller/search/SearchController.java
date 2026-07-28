package com.team1.appang.controller.search;

import com.team1.appang.dto.MessageResponse;
import com.team1.appang.dto.search.AutocompleteResponse;
import com.team1.appang.dto.search.SearchInitResponse;
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

import java.util.List;

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


    //검색페이지 초기화면 API
    @Operation(
            summary = "검색 페이지 초기 화면",
            description = "검색페이지 진입시 동적으로 변경되는 추천 검색어를 반환합니다. (최대 10개)"
    )
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = SearchInitResponse.class),
                    examples = @ExampleObject(value = """
            {
              "message": "추천 검색어 조회가 완료되었습니다.",
              "recommendKeywords": ["샐러드", "새벽배송 특가", "탄산수"]
            }
            """)))
    @GetMapping("/init")
    public ResponseEntity<?> getSearchInit() {
        List<String> keywords = searchService.getRecommendKeywords();
        return ResponseEntity.ok(new SearchInitResponse("추천 검색어 조회가 완료되었습니다.", keywords));
    }

    @Operation(
            summary = "검색 키워드 자동 완성",
            description = "사용자가 입력중인 문자열을 받아 그 문자열로 시작하는 상품명을 최대 10개까지 추천합니다."
    )
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = AutocompleteResponse.class),
                    examples = @ExampleObject(value = """
            {
              "message": "자동완성 목록 조회가 완료되었습니다.",
              "suggestions": ["바나나", "바나나칩", "바나나우유"]
            }
            """)))
    @GetMapping("/autocomplete")
    public ResponseEntity<?> getAutocomplete(
            @Parameter(description = "사용자가 입력중인 검색어", example = "바")
            @RequestParam String keyword
    ) {
        List<String> suggestions = searchService.getAutocompleteSuggestions(keyword);
        return ResponseEntity.ok(new AutocompleteResponse("자동완성 목록 조회가 완료되었습니다.", suggestions));
    }
}