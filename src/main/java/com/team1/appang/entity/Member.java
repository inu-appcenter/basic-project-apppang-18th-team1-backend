package com.team1.appang.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {


    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

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
    public Member(String email, String password, String username, String nickname, String phoneNumber) {
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
