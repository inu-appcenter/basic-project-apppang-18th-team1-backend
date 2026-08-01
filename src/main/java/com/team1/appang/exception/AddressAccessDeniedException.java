package com.team1.appang.exception;

//본인 소유가 아닌 배송지에 접근(수정/삭제/기본지정/주문 사용)하려 할 때
public class AddressAccessDeniedException extends RuntimeException {
    public AddressAccessDeniedException(){
        super("본인의 배송지만 이용할 수 있습니다.");
    }

    public AddressAccessDeniedException(String message) {
        super(message);
    }
}
