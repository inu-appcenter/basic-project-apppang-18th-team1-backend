package com.team1.appang.security;

//JWT 토큰을 생성하는 클래스

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

//스프링 빈 등록을 위한 Component
@Component
public class JwtTokenProvider {
    //토큰 sign에 사용할 비밀키
    private final SecretKey secretKey;

    //토큰 유효시간
    private final long accessTokenValidity = 3600000;//1시간
    private final long refreshTokenValidity = 1209600000; //2주

    //컨트롤러에서 토큰 유효시간을 쓸 수 있게 하기 위한 퍼블릭 메서드
    public long getRefreshTokenValidityInMilliseconds() {
        return this.refreshTokenValidity;
    }

    //생성자를 통해 application.yml에 설정한 jwt.secret값을 주입
    //보안을 위한 절차
    public JwtTokenProvider(@Value("${jwt.secret}") String secretString) {
        //문자열을 jjwt라이브러리가 사용할 수 있는 SecretKey 객체로 변환
        this.secretKey = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
    }

    //이메일 기반으로 Access Token 생성
    public String createAccessToken(String email) {
        return createToken(email, accessTokenValidity);
    }

    // 이메일 기반으로 Refresh Token 생성
    public String createRefreshToken(String email) {
        return createToken(email, refreshTokenValidity);
    }

    //JWT 토큰 생성 로직
    private String createToken(String email, long validityPeriod) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityPeriod);

        return Jwts.builder()
                .subject(email) //토큰 식별자는 이메일
                .issuedAt(now) //토큰 발행시간
                .expiration(validity) //토큰 만료 시간
                .signWith(secretKey) //비밀키로 암호화 서명
                .compact(); //String 형태로 토큰을 빌드
    }

    //토큰 유효성 검증 메서드
    public boolean validateToken(String token) {
        try{
            //비밀키로 서명을 검증하면서 토큰을 파싱함

            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        }catch (JwtException | IllegalArgumentException e){
            return false; //만약 서명이 틀리거나 만료되었다면 예외 발생으로 false반환
        }
    }

    //토큰에서 이메일 추출
    public String getEmail(String token){
        //비밀키 기반 파서를 생성
        return Jwts.parser().verifyWith(secretKey).build()
                //토큰을 파싱해서 객체인 Claims을 얻음
                .parseSignedClaims(token)
                //꺼낸 Claims을 꺼냄
                .getPayload()
                //토큰 생성에 넣었던 sub(=이메일)을 반환함
                .getSubject();
    }
}
