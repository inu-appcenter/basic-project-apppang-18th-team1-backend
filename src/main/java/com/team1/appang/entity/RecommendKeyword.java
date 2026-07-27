package com.team1.appang.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

//검색 페이지 초기 화면에 노출되는 추천 검색어
//관리자 API 없이 DB에 직접 SQL로 추가/삭제하는 방식으로 운영
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecommendKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String keyword;

    //노출 순서. 값이 작을수록 먼저 노출됨
    //피그마 확인결과 가나다순 나열이 아니여서 추가함
    @Column(nullable = false)
    private int sortOrder;

    @Builder
    public RecommendKeyword(String keyword, int sortOrder) {
        this.keyword = keyword;
        this.sortOrder = sortOrder;
    }
}