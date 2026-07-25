package com.team1.appang.dto.auth;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

//로그인 API Request DTO
//record 문법 사용
public record LoginRequest(
        @Schema(description = "로그인 이메일", example = "test@example.com")
        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email,
        @Schema(description = "비밀번호 (영문+숫자+특수문자 포함 8자 이상)", example = "password1!")
        @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
        @Pattern(regexp =  "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+|~={};:'<>,.?/]).{8,}$",
                message = "비밀번호 형식이 옳지 않습니다.")
        String password
){}