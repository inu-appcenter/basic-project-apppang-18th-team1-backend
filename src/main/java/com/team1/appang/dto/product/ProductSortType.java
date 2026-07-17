package com.team1.appang.dto.product;

//정렬 기준을 문자열로 안전하게 관리하기 위한 Enum
//프론트가 sort=latest, sort=priceLow 등의 문자열을 보내면 이 값으로 변환해서 사용
public enum ProductSortType {
    RANKING,   //랭킹순 (현재는 최신순과 동일하게 임시 처리)
    LATEST,    //최신순
    PRICE_LOW, //낮은 가격순
    PRICE_HIGH; //높은 가격순

    //프론트에서 온 문자열을 안전하게 Enum으로 변환
    //잘못된 값이 오면 기본값(LATEST)으로 처리
    public static ProductSortType from(String value) {
        if (value == null) return LATEST;
        try {
            return ProductSortType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return LATEST; //기본값은 LATEST
        }
    }
}