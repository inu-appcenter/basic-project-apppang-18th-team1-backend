package com.team1.appang.dto.auth;

//이메일을 찾아 반환하는 Response
public record FindEmailResponse(
        String email,
        String message
) {
}