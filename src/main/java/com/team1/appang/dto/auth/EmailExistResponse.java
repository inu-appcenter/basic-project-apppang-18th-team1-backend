package com.team1.appang.dto.auth;

import lombok.Getter;

//이메일 중복 확인 응답 DTO
@Getter
public class EmailExistResponse {

    //boolean은 null값이 불가능하고, Boolean은 객체이기에 null값을 가질 수 있다.
    //이메일 형식 위반의 경우 isAuailable은 값을 가지지 않으므로 Boolean을 사용
    private final Boolean isAvailable;
    private final String message;

    //내부 생성자
    private EmailExistResponse(Boolean isAvailable, String message){
        this.isAvailable = isAvailable;
        this.message = message;
    }

    //성공시
    public static EmailExistResponse success(boolean isAvailable, String message) {
        return new EmailExistResponse(isAvailable, message);
    }
    //이메일 형식 오류 등으로 실패시
    public static EmailExistResponse fail(String message) {
        return new EmailExistResponse(null, message);
    }



}
