package com.team1.appang.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

//로그인 Response DTO
public record LoginResponse(
        @Schema(description = "결과 메시지", example = "로그인에 성공했습니다.")
        String message,
        @Schema(description = "발급된 accessToken")
        String accessToken,
        @Schema(description = "발급된 refreshToken (쿠키로도 별도 전달됨)")
        String refreshToken
) {}