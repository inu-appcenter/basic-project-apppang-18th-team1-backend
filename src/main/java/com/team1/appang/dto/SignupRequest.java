package com.team1.appang.dto;

import lombok.Getter;

@Getter
public class SignupRequest {
    private String email;
    private String password;
    private String name;
    private String nickname;
    private String phoneNum;
}
