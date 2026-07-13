package com.team1.appang.dto.auth;

public record TokenReissueResponse(
        String message,
        String accessToken
) {}
