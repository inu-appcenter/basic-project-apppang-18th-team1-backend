package com.team1.appang.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

public record TokenReissueResponse(
        @Schema(description = "결과 메시지", example = "토큰이 재발급되었습니다.")
        String message,
        @Schema(description = "새로 발급된 accessToken")
        String accessToken
) {}