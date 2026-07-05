package com.team1.appang.repository;

import com.team1.appang.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

//인증 정보 테이블 접근용, 이메일 인증코드와 resetToken이 유효한지 검증할때 사용
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Integer> {

    // 이메일로 가장 최근의 인증 정보를 찾는 메서드
    Optional<EmailVerification> findByEmail(String email);

    // 토큰으로 인증 정보를 찾는 메서드
    Optional<EmailVerification> findByResetToken(String resetToken);
}