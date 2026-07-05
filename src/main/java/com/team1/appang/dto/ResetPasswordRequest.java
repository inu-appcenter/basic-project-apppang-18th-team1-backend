package com.team1.appang.dto;

//패스워드 재설정에서 Request를 받을 DTO

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {
    private String email; //사용자 이메일
    private String newPassword; //변경 비밀번호
    private String passwordCheck; //새 비밀번호 확인
    private String resetToken;//본인 인증 완료 토큰
}
