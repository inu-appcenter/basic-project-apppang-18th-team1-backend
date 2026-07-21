package com.team1.appang.exception;

//이미 가입된 이메일
public class DuplicateEmailException extends RuntimeException{
    public DuplicateEmailException(){
        super("이미 가입된 이메일입니다.");
    }

    public DuplicateEmailException(String message){
        super(message);
    }
}
