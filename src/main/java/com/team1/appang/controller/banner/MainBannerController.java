package com.team1.appang.controller.banner;

import com.team1.appang.dto.MessageResponse;
import com.team1.appang.dto.banner.MainBannerResponse;
import com.team1.appang.service.banner.MainBannerService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "MainBanner", description = "메인페이지 배너 조회 API")
@RestController
@RequestMapping("/api/main-banners")
@RequiredArgsConstructor
public class MainBannerController {

    private final MainBannerService mainBannerService;

    //메인페이지 배너 조회 API
    //판매량(취소되지 않은 주문 기준) 내림차순, 기본 3개
    @Operation(summary = "메인 배너 조회", description = "판매량(취소되지 않은 주문 기준) 상위 상품을 메인페이지 배너로 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = MainBannerResponse.class))),
            @ApiResponse(responseCode = "400", description = "limit 값이 허용 범위를 벗어남",
                    content = @Content(examples = @ExampleObject(value = """
                {
                  "message": "limit은 1~10 사이여야 합니다."
                }
                """)))
    })
    @GetMapping
    public ResponseEntity<?> getMainBanners(
            @Parameter(description = "조회할 배너 개수 (기본값 3)", example = "3")
            @RequestParam(defaultValue = "3") int limit) {

        if (limit < 1 || limit > 10) {
            return ResponseEntity.badRequest().body(new MessageResponse("limit은 1~10 사이여야 합니다."));
        }

        MainBannerResponse response = mainBannerService.getMainBanners(limit);
        return ResponseEntity.ok(response);
    }
}
