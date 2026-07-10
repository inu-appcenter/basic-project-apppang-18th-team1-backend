package com.team1.appang.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

//한번 담기는 용도이므로 class대신 record를 사용
public record EmailCheckRequest(
        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email
) {}
