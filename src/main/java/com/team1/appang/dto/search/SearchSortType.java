package com.team1.appang.dto.search;

//검색 정렬 기준
public enum SearchSortType {
    RANKING,
    LATEST,
    LOW_PRICE,
    HIGH_PRICE;

    //잘못된 값이 들어오면 RANKING을 기본값으로 사용
    public static SearchSortType from(String value) {
        try {
            return SearchSortType.valueOf(value);
        } catch (IllegalArgumentException | NullPointerException e) {
            return RANKING;
        }
    }
}