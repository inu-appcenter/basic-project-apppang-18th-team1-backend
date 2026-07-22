package com.team1.appang.exception;

//장바구니에 담은 상품의 개수가 잔여 상품 개수보다 많을때의 예외처리
public class CartQuantityExceededException extends RuntimeException {
    public CartQuantityExceededException(){
        super("장바구니에 담을 수 있는 수량을 초과했습니다.");
    }
    public CartQuantityExceededException(String message) {
        super(message);
    }
}