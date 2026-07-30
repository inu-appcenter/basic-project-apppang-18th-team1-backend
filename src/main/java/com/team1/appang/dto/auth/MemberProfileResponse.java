package com.team1.appang.dto.auth;

import com.team1.appang.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;

//내 프로필 조회 응답
public record MemberProfileResponse(
        @Schema(description = "이름", example = "홍길동")
        String name,
        @Schema(description = "닉네임", example = "길동이")
        String nickname,
        @Schema(description = "이메일", example = "test@example.com")
        String email,
        @Schema(description = "전화번호", example = "010-1234-5678")
        String phoneNumber,
        @Schema(description = "결과 메시지", example = "회원 정보를 조회했습니다.")
        String message
) {
    public static MemberProfileResponse from(Member member) {
        return new MemberProfileResponse(
                member.getName(),
                member.getNickname(),
                member.getEmail(),
                member.getPhoneNumber(),
                "회원 정보를 조회했습니다."
        );
    }
}
