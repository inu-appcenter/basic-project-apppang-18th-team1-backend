package com.team1.appang.exception;

//요청한 수량이 0 이하 등, 유효하지 않을 때 사용하는 Exception
public class InvalidQuantityException extends RuntimeException{
    public InvalidQuantityException(String message){
        super(message);
    }
}
