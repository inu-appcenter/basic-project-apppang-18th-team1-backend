package com.team1.appang.service;

import com.team1.appang.entity.EmailVerification;
import com.team1.appang.entity.User;
import com.team1.appang.repository.EmailVerificationRepository;
import com.team1.appang.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/*
==================================
설명: user의 비밀전호(인증) 관련 서비스를 모아놓은 클래스
기능: user의 응답을 받고 길이, 동일함, 토큰, 이메일주소등을 검증한 뒤 비밀번호를 재설정함
==================================
 */
@Service
@RequiredArgsConstructor //생성자 자동 생성
public class UserPasswordService {

    private final UserRepository userRepository;
    private final EmailVerificationRepository emailVerificationRepository;

    /*비밀번호 재설정 기능 로직
    1. 비밀번호 길이 확인
    2. 비밀번호와 재확인 비밀번호가 같은지 체크
    3. 토큰이 유효한지 확인
    4. 이메일 주소가 맞는지 확인
    5. 존재하는 회원인지 확인
    6. 다 통과하면 비밀번호 변경 실행
    7. 토큰 무효화
     */
    @Transactional //DB 값을 수정해야하므로
    public String resetPassword(String email, String newPassword, String passwordCheck, String resetToken) {
        if (newPassword.length() < 8){
            return("비밀번호는 최소 8자리 이상이어야 합니다.");
        }

        if (!newPassword.equals(passwordCheck)){
            return("비밀번호가 일치하지 않습니다.");
        }

        Optional<EmailVerification> verificationOpt = emailVerificationRepository.findByResetToken(resetToken);
        if(verificationOpt.isEmpty()){
            return ("만료되거나 유효하지 않은 요청입니다.");
        }

        EmailVerification verification = verificationOpt.get();
        if (!verification.getEmail().equals(email)){
            return ("이메일 정보가 일치하지 않습니다.");
        }

        Optional<User> userOpt = userRepository.findByEamil(email);
        if(userOpt.isEmpty()){
            return("존재하지 않는 회원입니다.");
        }

        //위 조건을 모두 통과한다면 비밀번호 변경을 실행
        User user = userOpt.get();
        user.setPassword(newPassword);

        //토큰 삭제
        emailVerificationRepository.delete(verification);
        return("비밀번호가 재설정 되었습니다.");
    }
}
