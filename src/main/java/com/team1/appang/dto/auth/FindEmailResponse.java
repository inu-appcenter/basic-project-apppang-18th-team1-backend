package com.team1.appang.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

//이메일을 찾아 반환하는 Response
public record FindEmailResponse(
        @Schema(description = "조회된 이메일", example = "test@example.com")
        String email,
        @Schema(description = "결과 메시지", example = "이메일을 찾았습니다.")
        String message
) {
}