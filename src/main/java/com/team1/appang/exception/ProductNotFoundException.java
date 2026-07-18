package com.team1.appang.exception;

//요청한 상품이 존재하지 않을 때
public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String message) {
        super(message);
    }
}