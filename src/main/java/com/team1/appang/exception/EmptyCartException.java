package com.team1.appang.exception;

//주문을 생성하려는데 장바구니에 선택된(isSelected) 상품이 하나도 없을 때
public class EmptyCartException extends RuntimeException {
    public EmptyCartException() {
        super("주문할 상품을 선택해주세요.");
    }

    public EmptyCartException(String message) {
        super(message);
    }
}
