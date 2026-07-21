package com.team1.appang.exception;

//상품 옵션을 찾을 수 없을 때의 Exception
public class ProductOptionNotFoundException extends RuntimeException {
    public ProductOptionNotFoundException(String message) {
        super(message);
    }
}
