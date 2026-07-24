package com.team1.appang.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

//이메일 찾기에 사용하는 DTO
public record FindEmailRequest (
        @Schema(description = "가입 시 등록한 이름", example = "홍길동")
        @NotBlank(message = "이름을 입력해주세요")
        String name,
        @Schema(description = "가입 시 등록한 전화번호 (숫자만)", example = "01012345678")
        @NotBlank(message = "전화번호를 입력해주세요")
        @Pattern(regexp = "^\\d+$", message = "전화번호는 숫자만 입력해주세요.")
        String phoneNumber
){



}