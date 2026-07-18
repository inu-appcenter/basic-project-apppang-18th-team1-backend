package com.team1.appang.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/*
'org.springframework.boot:spring-boot-starter-security'
의존성 추가로 인해 로그인을 하지 않으면 접근이 차단되는 문제 발생
해결하기 위해 해당 클래스 추가
 */
@Configuration //컴포넌트 스캔에 인식되기 위해 추가
@EnableWebSecurity //스프링 시큐리티 설정을 활성화함
public class SecurityConfig {

    //JWT 토큰을 검증하고 인증 정보를 SecurityContext에 등록해주는 커스텀 필터
    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    //비밀번호 암호화
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                //CSRF 보호기능, 로그인창 폼, HTTP 기본 인증 비활성화
                .csrf(csrf -> csrf.disable())
                .formLogin(form->form.disable())
                .httpBasic(basic -> basic.disable())

                //서버측에 세션을 저장하거나 유지하지 않도록 설정함
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //"/api/auth"로 시작하는 모든 경로는 접근 가능
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        //그 외는 접근 차단. 이후 허가할 기능이 생기면 추가
                        .anyRequest().authenticated()
                )
                //JwtFilter를 UsernamePasswordAuthenticationFilter 앞에 등록
                //-> Authorization 헤더의 토큰을 검증하고 인증 정보를 세팅하는 로직이 실제로 실행되도록 함
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}