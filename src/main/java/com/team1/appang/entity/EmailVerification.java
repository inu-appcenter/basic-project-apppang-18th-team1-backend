package com.team1.appang.entity;
/*
================================
* 이메일 인증에 사용되는 entity
* email_verifications라는 임시 테이블에 만들어 저장
================================
 */


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "email_verifications")
@Getter
@Setter
@NoArgsConstructor
public class EmailVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //인증 요청자의 이메일 주소
    @Column(nullable = false)
    private String email;

    //사용자 메일로 보낸 문자 인증 코드
    private String code;
    //임시 허가 토큰
    private String resetToken;

    //인증 요청 성공 여부
    @Column(nullable = false)
    private boolean isVerified = false;
}
