package com.team1.appang.repository;

import com.team1.appang.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long>{
    Optional<Member> findByEmail(String email);
}
