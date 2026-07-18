package com.team1.appang.entity;

//배송 타입을 나타내는 Enum
//오타나 값 불일치를 방지하기 위해 문자열 대신 Enum으로 관리
public enum ShippingType {
    ROCKET("로켓배송"),
    ROCKET_FRESH("로켓프레시"),
    ROCKET_DAWN("새벽배송"),
    GENERAL("일반배송");

    private final String displayName; //한글 명

    ShippingType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}