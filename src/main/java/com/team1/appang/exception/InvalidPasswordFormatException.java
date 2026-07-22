package com.team1.appang.exception;

//비밀번호 형식이 규칙에 맞지 않을 때
public class InvalidPasswordFormatException extends RuntimeException {
    public InvalidPasswordFormatException(){
        super("비밀번호 형식이 옳지 않습니다.");
    }

    public InvalidPasswordFormatException(String message) {
        super(message);
    }
}