package com.team1.appang.repository;

import com.team1.appang.entity.RecommendKeyword;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecommendKeywordRepository extends JpaRepository<RecommendKeyword, Long> {

    //sortOrder 오름차순으로 정렬해서 조회. Pageable로 최대 개수(10개)를 제한함
    //피그마에서 추천 검색어 개수가 10개라서 해당 개수를 기준으로 제외함. 추후 수정 가능
    List<RecommendKeyword> findAllByOrderBySortOrderAsc(Pageable pageable);
}