package com.team1.appang.service;

import com.team1.appang.dto.SignupRequest;
import com.team1.appang.entity.Member;
import com.team1.appang.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public void signup(SignupRequest request) {
        // 1. 이메일 중복 검사
        if (memberRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다");
        }

        // 2. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 3. 회원 엔티티 생성
        Member member = new Member(
                request.getEmail(),
                encodedPassword,
                request.getName(),
                request.getNickname(),
                request.getPhoneNum()
        );

        // 4. DB에 저장
        memberRepository.save(member);
    }
}