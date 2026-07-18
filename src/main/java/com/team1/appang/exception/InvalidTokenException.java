package com.team1.appang.exception;

//Supabase 토큰 검증에 실패했을 때
public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
    }
}