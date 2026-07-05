package com.team1.appang.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * ==========================================
 * 엔티티 명  : User (회원)
 * 테이블 구성:
 * id(PK)
 * username(사용자 이름), password(비밀번호), nickname(닉네임)
 * email(이메일), phoneNumber(전화번호)
 * ==========================================
 */
@Entity
@Table(name = "users") //MySQL에 'users' 테이블 만드는 코드
@Getter
@Setter
@NoArgsConstructor //기본 생성자 생성
public class User {

    //PK: ID, 우선은 자동으로 1씩 증가하게 설정
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; //Long이나 Integer 타입으로 하는 경우도 있음

    //username은 겹칠 수 있으므로 unique지정 안함
    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    //이메일 하나당 하나의 계정만 생성가능
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phoneNumber;

}
