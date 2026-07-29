package com.team1.appang.exception;

//이미 배송완료/취소/환불된 주문은 다시 취소할 수 없음
public class OrderCancelNotAllowedException extends RuntimeException {
    public OrderCancelNotAllowedException() {
        super("취소할 수 없는 주문 상태입니다.");
    }

    public OrderCancelNotAllowedException(String message) {
        super(message);
    }
}
