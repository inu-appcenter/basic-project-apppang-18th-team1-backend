package com.team1.appang.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

//회원가입 응답 DTO
public record SignUpResponse(
        @Schema(description = "생성된 회원 id", example = "1")
        Long id,
        @Schema(description = "결과 메시지", example = "회원가입이 성공적으로 완료되었습니다.")
        String message,
        @Schema(description = "실패한 필드명 (성공 시 null)", example = "null")
        String field) {


    //성공했을 때에 사용하는 응답 생성 메서드
    //id와 성공 메시지를 반환
    public static SignUpResponse success(Long id, String message){
        return new SignUpResponse(id, message, null);
    }

}
