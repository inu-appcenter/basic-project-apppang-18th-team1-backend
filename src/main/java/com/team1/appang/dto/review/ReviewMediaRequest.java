package com.team1.appang.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;

public record ReviewMediaRequest(
        @Schema(description = "미디어 URL", example = "https://.../photo1.jpg")
        String mediaUrl,
        @Schema(description = "미디어 타입",example = "image")
        String mediaType,
        @Schema(description = "재생 시간(초), 이미지면 0", example = "0")
        int duration) {

}
