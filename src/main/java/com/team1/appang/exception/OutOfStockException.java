package com.team1.appang.exception;

//주문하려는 수량이 남은 재고보다 많을 때의 예외처리
public class OutOfStockException extends RuntimeException {
    public OutOfStockException() {
        super("재고가 부족합니다.");
    }

    public OutOfStockException(String message) {
        super(message);
    }
}
