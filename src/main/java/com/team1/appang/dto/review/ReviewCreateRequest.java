package com.team1.appang.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ReviewCreateRequest(
        @Schema(description = "평점", example = "5")
        int rating,
        @Schema(description = "리뷰 내용", example = "착용감이 좋아요")
        @NotBlank(message = "리뷰 내용을 작성해주세요.")
        @Size(max = 1000, message = "리뷰 내용은 1000자를 초과할 수 없습니다.")
        String content,
        @Schema(description = "리뷰 미디어 리스트(이미지 등)")
        List<ReviewMediaRequest> mediaList
) {
}
