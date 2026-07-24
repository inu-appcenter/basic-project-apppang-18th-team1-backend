package com.team1.appang.exception;

//상품 옵션을 찾을 수 없을 때의 Exception
public class ProductOptionNotFoundException extends RuntimeException {
    public ProductOptionNotFoundException(){
        super("해당하는 옵션을 찾을 수 없습니다.");
    }
    public ProductOptionNotFoundException(String message) {
        super(message);
    }
}
