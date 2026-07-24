package com.team1.appang.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

//한번 담기는 용도이므로 class대신 record를 사용
public record EmailCheckRequest(
        @Schema(description = "중복 확인할 이메일", example = "test@example.com")
        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email
) {}