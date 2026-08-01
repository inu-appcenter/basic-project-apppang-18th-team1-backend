package com.team1.appang.exception;

//요청한 배송지를 찾을 수 없을 때
public class AddressNotFoundException extends RuntimeException {
    public AddressNotFoundException(){
        super("해당하는 배송지를 찾을 수 없습니다.");
    }

    public AddressNotFoundException(String message) {
        super(message);
    }
}
