package com.team1.appang.exception;

//주문이 존재하지 않거나, 본인 소유가 아닐 때
//두 경우 모두 같은 예외/메시지를 사용해 남의 주문 ID로 존재 여부를 추측하는 걸 방지
public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException() {
        super("잘못된 접근입니다.");
    }

    public OrderNotFoundException(String message) {
        super(message);
    }
}
