package com.team1.appang.dto.auth;

//패스워드 재설정에서 Request를 받을 DTO

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResetPasswordRequest {

    @Schema(description = "사용자 이메일", example = "test@example.com")
    @NotBlank(message = "이메일을 입력해주세요.")
    private String email; //사용자 이메일

    @Schema(description = "변경할 새 비밀번호", example = "newPassword1!")
    @NotBlank(message = "새 비밀번호를 입력해주세요.")
    private String newPassword; //변경 비밀번호

    @Schema(description = "새 비밀번호 확인", example = "newPassword1!")
    @NotBlank(message = "비밀번호 확인을 입력해주세요.")
    private String passwordCheck; //새 비밀번호 확인

    @Schema(description = "Supabase로부터 발급받은 인증 토큰")
    @NotBlank(message = "인증 토큰이 필요합니다.")
    private String supabaseToken;   // 프론트엔드가 Supabase로부터 받은 Access Token
}