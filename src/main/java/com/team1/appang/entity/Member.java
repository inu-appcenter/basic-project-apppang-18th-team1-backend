package com.team1.appang.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
 * ==========================================
 * 엔티티 명  : Member (회원)
 * 테이블 구성:
 * id(PK)
 * username(사용자 이름), password(비밀번호), nickname(닉네임)
 * email(이메일), phoneNumber(전화번호)
 * ==========================================
 */
@Entity
@Table(name = "Member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 접근을 막아 안전성 확보
public class Member { // 혹은 Member (팀 내 컨벤션에 맞게 선택)

    //Supabase Auth 의 UUID 문자열이 들어옴
    @Id
    private String id;

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

    @Builder
    public Member(String id, String email, String password, String username, String nickname, String phoneNumber) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.username = username;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
    }

    // 비밀번호 변경을 위한 메서드
    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

}
