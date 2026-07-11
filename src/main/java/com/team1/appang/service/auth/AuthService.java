package com.team1.appang.service.auth;

import com.team1.appang.dto.auth.*;
import com.team1.appang.entity.Member;
import com.team1.appang.repository.MemberRepository;
import com.team1.appang.security.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
@Transactional //DB 값을 수정해야하므로
public class AuthService {
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider; //jwt 사용 필요
    private final PasswordEncoder passwordEncoder; //비밀번호 암호화 작업을 위함


    //이메일 중복 검사, 검사후 값을 true/false로 반환
    public boolean isEmailExists(String email){
        return memberRepository.existsByEmail(email);
    }

    //전화번호로 이메일 찾기 로직
    public FindEmailResponse findEmail(FindEmailRequest request) {
        //전화번호로 회원을 찾음
        Member member = memberRepository.findByPhoneNumber(request.phoneNumber())
                .orElseThrow(() -> new IllegalArgumentException("일치하는 회원 정보가 없습니다."));

        //찾은회원과 이름이 동일한지 검증
        if (!member.getName().equals(request.name())){
            throw new IllegalArgumentException("일치하는 회원 정보가 없습니다.");
        }

        String email = member.getEmail();

        return new FindEmailResponse(email, null);
    }


    //로그인 로직
    public LoginResponse login(LoginRequest request){

        //이메일로 사용자를 찾고 비밀번호를 검증함.
        //이때 보안을 위해 메시지 내용은 동일한 내용으로 사용
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(()-> new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다"));

        if (!passwordEncoder.matches(request.password(), member.getPassword())){
            throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다");
        }

        //인증 성공시 토큰 발행
        String accessToken = jwtTokenProvider.createAccessToken(member.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getEmail());

        //컨트롤러로 토큰과 메시지 반환
        return new LoginResponse("로그인에 성공했습니다.", accessToken, refreshToken);
    }

    //회원가입 로직
    public Long signup(SignUpRequest request) {
        //이메일 중복 검사 진행
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다."); //중복 오류
        }

        //비밀번호 암호화 추가
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        //엔티티에 유저 정보를 담음
        Member member = Member.builder()
                .email(request.getEmail())
                .password(encodedPassword) //비밀번호 암호화 진행
                .nickname(request.getNickname())
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .build();

        Member savedMember = memberRepository.save(member);

        return savedMember.getId();
    }
}

