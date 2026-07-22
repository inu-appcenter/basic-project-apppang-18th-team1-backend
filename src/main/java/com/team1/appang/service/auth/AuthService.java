package com.team1.appang.service.auth;

import com.team1.appang.dto.auth.*;
import com.team1.appang.entity.Member;
import com.team1.appang.exception.*;
import com.team1.appang.repository.MemberRepository;
import com.team1.appang.security.JwtTokenProvider;
import com.team1.appang.security.RefreshTokenStore; //로그아웃 시 refreshToken을 무효화하기 위해 추가
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication; //잘못된 import 수정
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Spring 트랜잭션으로 수정
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
//로그를 위해 추가
@Slf4j
@Service
@RequiredArgsConstructor //생성자 자동생성
//클래스 전체의 기본 트랜잭션 설정을 읽기 전용으로 지정
@Transactional(readOnly = true)
public class AuthService {
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider; //jwt 사용 필요
    private final PasswordEncoder passwordEncoder; // 비밀번호 암호화 작업을 위함
    private final RefreshTokenStore refreshTokenStore; //로그아웃 시 서버 측에서 refreshToken을 무효화하기 위해 추가


    //토큰 재발급 로직
    public TokenReissueResponse reissue (String refreshToken){
        //쿠기가 없거나 토큰이 만료되었다면 예외 처리
        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)){
            throw new InvalidTokenException("다시 로그인해주세요."); //만료된 토큰 예외 클래스
        }
        //토큰이 유효하다면 이메일을 꺼내 실제 회원이 존재하는지 DB에서 확인
        String email = jwtTokenProvider.getEmail(refreshToken);
        Member member = memberRepository.findByEmail(email)
                //없다면 예외처리
                .orElseThrow(()-> new MemberNotFoundException("잘못된 접근입니다."));

        //Redis에 저장된 refreshToken과 일치하는지 확인
        //로그아웃 되었거나 서버가 발급하지 않은 토큰이라면 여기서 걸러짐
        //stateless JWT의 한계(로그아웃해도 토큰 자체는 만료 전까지 유효한 문제)를 보완하는 부분
        if (!refreshTokenStore.isValid(email, refreshToken)) {
            throw new InvalidTokenException();
        }

        //모두 통과한다면 뽑아낸 이메일로 새로운 토큰을 발급
        String newAccessToken = jwtTokenProvider.createAccessToken(member.getEmail());
        //메시지와 토큰을 담아 응답 반환
        return new TokenReissueResponse("토큰이 재발급되었습니다.", newAccessToken);
    }

    //이메일 중복 검사, 검사후 값을 true/false로 반환
    public boolean isEmailExists(String email){
        return memberRepository.existsByEmail(email);
    }

    //전화번호로 이메일 찾기 로직
    public FindEmailResponse findEmail(FindEmailRequest request) {
        //전화번호로 회원을 찾음
        Member member = memberRepository.findByPhoneNumber(request.phoneNumber())
                .orElseThrow(() -> new MemberNotFoundException());
        //찾은회원과 이름이 동일한지 검증
        if (!member.getName().equals(request.name())){
            throw new MemberNotFoundException();
        }

        String email = member.getEmail();
        return new FindEmailResponse(email, null);
    }

    //로그인 로직
    public LoginResponse login(LoginRequest request){
        //이메일로 사용자를 찾고 비밀번호를 검증함.
        // 이때 보안을 위해 메시지 내용은 동일한 내용으로 사용
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(()-> new MemberNotFoundException());

        if (!passwordEncoder.matches(request.password(), member.getPassword())){
            throw new PasswordMismatchException();
        }
        //인증 성공시 토큰 발행
        String accessToken = jwtTokenProvider.createAccessToken(member.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getEmail());

        //발급한 refreshToken을 Redis에 저장 (로그아웃 시 무효화하기 위한 기준값)
        //만료시간은 refreshToken 자체의 유효기간과 동일하게 맞춤
        refreshTokenStore.save(member.getEmail(), refreshToken,
                jwtTokenProvider.getRefreshTokenValidityInMilliseconds());

        //컨트롤러로 토큰과 메시지 반환
        return new LoginResponse("로그인에 성공했습니다.", accessToken, refreshToken);
    }

    //로그아웃 로직
    //stateless JWT는 로그아웃해도 토큰 자체가 만료 전까지 유효하다는 한계가 있어
    //Redis에 저장해둔 refreshToken을 지워서 이후 재발급이 불가능하도록 처리
    public void logout(String refreshToken) {
        //쿠키가 없거나 이미 유효하지 않은 토큰이면 지울 대상이 없으므로 그냥 종료
        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            return;
        }
        String email = jwtTokenProvider.getEmail(refreshToken);
        //Redis에서 해당 회원의 refreshToken 삭제
        refreshTokenStore.delete(email);
    }

    //회원가입 로직
    //실제 DB를 수정해야하므로 Transactional 추가
    @Transactional
    public Long signup(SignUpRequest request) {
        //이메일 중복 검사 진행
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException();
        }
        //전화번호 당 하나의 계정만 생성 가능하므로 전화번호 중복 검사 로직 추가
        if (memberRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new DuplicatePhoneNumberException();
        }

        //비밀번호 암호화 추가
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        //엔티티에 유저 정보를 담음
        Member member = Member.builder()
                .email(request.getEmail())
                .password(encodedPassword)  //비밀번호 암호화 진행
                .nickname(request.getNickname())
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .build();

        Member savedMember = memberRepository.save(member);
        return savedMember.getId();
    }

    //로그인한 회원의 id를 받환받아 존재하는 회원인지 검증
    //비로그인 상태라면 null
    public Long getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        String email = (String) authentication.getPrincipal();
        return memberRepository.findByEmail(email)
                .map(Member::getId)
                .orElse(null);
    }
}