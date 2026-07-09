package com.team1.appang.repository;

import com.team1.appang.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


//JpaRepository를 상속
// <테이블 명, PK 타입>
public interface MemberRepository extends JpaRepository<Member, String> {
    //비밀번호 재변경등을 위해 멤버 데이터를 가져옴
    Optional<Member> findByEmail(String email);

    //이메일 중복 조회용으로 있는지 여부만 확인
    boolean existsByEmail(String email);
}
