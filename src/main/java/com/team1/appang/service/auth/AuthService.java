package com.team1.appang.service.auth;

import com.team1.appang.dto.auth.SignUpRequest;
import com.team1.appang.entity.Member;
import com.team1.appang.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/*
=========================================
 설 명:
 회원가입 / 로그인 / 로그아웃을 모두 관리하는 서비스
 각 API별로 분리하면 파일 개수가 너무 많아질듯해 하나로 관리
 비밀번호 재설정은 이미 완성된 상태라 분리를 유지
 ========================================
 */
@Slf4j  //로그를 위해 추가
@Service
@RequiredArgsConstructor //생성자 자동 생성
public class AuthService {
    private final MemberRepository memberRepository;

    //회원가입 로직
    @Transactional //DB 값을 수정해야하므로
    public Long singup(SignUpRequest request) {
        //이메일 중복 검사 진행
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다."); //중복 오류
        }


        //엔티티에 유저 정보를 담음
        Member member = Member.builder()
                .email(request.getEmail())
                .password(request.getPassword()) //일단은 암호화 없이 진행
                .nickname(request.getNickname())
                .username(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .build();

        Member savedMember = memberRepository.save(member);

        return savedMember.getId();
    }
}

