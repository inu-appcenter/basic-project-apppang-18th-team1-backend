package com.team1.appang.exception;

//장바구니 항목이 존재하지 않거나, 본인 소유가 아닐 때
//두 경우 모두 같은 예외/메시지를 사용해 남의 항목 ID로 존재 여부를 추측하는 걸 방지
public class CartItemNotFoundException extends RuntimeException {
    public CartItemNotFoundException(String message) {
        super(message);
    }

    public CartItemNotFoundException(){
        super("잘못된 접근입니다.");
    }
}