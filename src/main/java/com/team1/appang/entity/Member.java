package com.team1.appang.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
public class Member {

    @Id
    private String id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String nickname;

    //이메일 하나당 하나의 계정만 생성가능
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phoneNum;

    @Builder
    public Member(String id, String email, String password, String username, String nickname, String phoneNum, String name) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.username = username;
        this.nickname = nickname;
        this.phoneNum = phoneNum;
        this.name = name;
    }

    // 비밀번호 변경을 위한 메서드
    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }
}