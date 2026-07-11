package com.team1.appang.dto.auth;

//로그인 Response DTO
public record LoginResponse(
        String message,
        String accessToken,
        String refreshToken
) {}
