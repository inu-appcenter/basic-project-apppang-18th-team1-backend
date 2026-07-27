package com.team1.appang.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;

public record ReviewHelpfulToggleResponse(
        @Schema(description = "토글 후 최종 도움돼요 상태", example = "true")
        boolean isHelpful,
        @Schema(description = "토글 후 변경된 도움돼요 수", example = "5")
        int helpfulCount,
        @Schema(description = "결과 메시지", example = "도움돼요를 눌렀습니다")
        String message
        ) {
    public static ReviewHelpfulToggleResponse added(int helpfulCount) {
        return new ReviewHelpfulToggleResponse(true, helpfulCount, "도움돼요를 눌렀습니다.");
    }

    public static ReviewHelpfulToggleResponse removed(int helpfulCount) {
        return new ReviewHelpfulToggleResponse(false, helpfulCount, "도움돼요를 취소했습니다.");
    }
}
