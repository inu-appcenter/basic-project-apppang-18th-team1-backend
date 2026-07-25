package com.team1.appang.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

//이메일 중복 확인 응답 DTO
public record EmailExistResponse (
        @Schema(description = "사용가능 여부", example = "True")
        Boolean isAvailable,
        @Schema(description = "결과 메시지")
        String message
){

    //성공시
    public static EmailExistResponse success(boolean isAvailable, String message) {
        return new EmailExistResponse(isAvailable, message);
    }


}
