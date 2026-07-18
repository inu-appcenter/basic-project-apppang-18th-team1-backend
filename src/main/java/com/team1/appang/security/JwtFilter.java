package com.team1.appang.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

//매 요청마다 한 번씩 실행되는 필터
//Authorization 헤더의 토큰을 꺼내 검증하고, 유효하면 인증 정보를 등록함
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        //Authorization 헤더 꺼내기
        String authorizationHeader = request.getHeader("Authorization");

        //헤더가 있고 "Bearer "로 시작하는 경우에만 처리
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            //"Bearer " 부분을 잘라내고 순수 토큰만 추출
            String token = authorizationHeader.substring(7);

            //토큰이 유효한지 검증
            if (jwtTokenProvider.validateToken(token)) {
                //토큰에서 이메일(사용자 식별자) 추출
                String email = jwtTokenProvider.getEmail(token);

                //인증 객체 생성
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());

                //SecurityContext에 인증 정보 등록
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            //토큰이 유효하지 않으면 그냥 인증 정보를 세팅하지 않고 넘어감
        }

        //다음 필터로 요청을 넘김
        filterChain.doFilter(request, response);
    }
}