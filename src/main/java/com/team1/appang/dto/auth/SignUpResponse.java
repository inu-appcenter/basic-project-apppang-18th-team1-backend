package com.team1.appang.dto.auth;

import lombok.Getter;

//회원가입 응답 DTO
@Getter
public class SignUpResponse {

    private final Long id;
    private final String message;
    private final String field;

    //내부생성자
    private SignUpResponse(Long id, String message, String field){
        this.id = id;
        this.message = message;
        this.field = field;
    }

    //성공했을 때에 사용하는 응답 생성 메서드
    //id와 성공 메시지를 반환
    public static SignUpResponse success(Long id, String message){
        return new SignUpResponse(id, message, null);
    }


}
