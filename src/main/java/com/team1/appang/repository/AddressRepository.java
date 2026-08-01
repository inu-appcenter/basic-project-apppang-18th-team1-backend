package com.team1.appang.repository;

import com.team1.appang.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {

    //회원의 배송지 목록 조회. 기본 배송지가 먼저, 그 다음 최신 등록순으로 정렬
    List<Address> findByMemberIdOrderByIsDefaultDescCreatedAtDesc(Long memberId);

    //현재 기본 배송지로 지정된 배송지 조회 (새 기본 배송지를 지정할 때 기존 기본 배송지를 해제하기 위함)
    Optional<Address> findByMemberIdAndIsDefaultTrue(Long memberId);
}
