package com.team1.appang.exception;

//해당 회원이 존재하지 않을 때
public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException() {
        super("일치하는 회원 정보가 없습니다.");
    }
    public MemberNotFoundException(String message) {
        super(message);
    }
}