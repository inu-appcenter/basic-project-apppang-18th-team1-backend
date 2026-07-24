package com.team1.appang.exception;

//이미 가입된 전화번호
public class DuplicatePhoneNumberException extends RuntimeException{
    public DuplicatePhoneNumberException(){
        super("이미 가입된 전화번호입니다.");
    }

    public DuplicatePhoneNumberException(String message){
        super(message);
    }

}
