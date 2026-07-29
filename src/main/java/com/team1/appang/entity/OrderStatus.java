package com.team1.appang.entity;

//주문 상태 + 배송 진행 단계를 함께 나타내는 Enum
public enum OrderStatus {
    ORDERED("주문완료"),
    PREPARING("상품준비중"),
    IN_TRANSIT("배송중"),
    DELIVERED("배송완료"),
    CANCELLED("주문취소");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
