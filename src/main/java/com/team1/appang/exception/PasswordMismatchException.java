package com.team1.appang.exception;

//새 비밀번호와 확인 비밀번호가 일치하지 않을 때
public class PasswordMismatchException extends RuntimeException {
    public PasswordMismatchException(String message) {
        super(message);
    }
}