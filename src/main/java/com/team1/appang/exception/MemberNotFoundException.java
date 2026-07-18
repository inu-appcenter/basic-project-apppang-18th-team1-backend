package com.team1.appang.exception;

//해당 이메일의 회원이 존재하지 않을 때
public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException(String message) {
        super(message);
    }
}