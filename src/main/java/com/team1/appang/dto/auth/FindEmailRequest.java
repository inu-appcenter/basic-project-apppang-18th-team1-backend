package com.team1.appang.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

//이메일 찾기에 사용하는 DTO
public record FindEmailRequest (
        @NotBlank(message = "이름을 입력해주세요")
        String name,
        @NotBlank(message = "전화번호를 입력해주세요")
        @Pattern(regexp = "^\\d+$", message = "전화번호는 숫자만 입력해주세요.")
        String phoneNumber
){



}
