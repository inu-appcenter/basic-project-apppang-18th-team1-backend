package com.team1.appang.exception;

public class ProductOptionMismatchException extends RuntimeException {
    public ProductOptionMismatchException() {
        super("상품과 옵션 정보가 일치하지 않습니다.");
    }
    public ProductOptionMismatchException(String message) {
        super(message);
    }
}