package com.team1.appang.repository;

import com.team1.appang.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


//JpaRepository를 상속
// <테이블 명, PK 타입>
public interface MemberRepository extends JpaRepository<Member, String> {
    Optional<Member> findByEmail(String email);

}
