package com.team1.appang.security;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

//refreshToken을 Redis에 저장하고 로그아웃 시 무효화하기 위한 클래스
@Component
@RequiredArgsConstructor
public class RefreshTokenStore {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String PREFIX = "refreshToken:";

    //로그인 시 refreshToken 저장 (email을 키로 사용)
    public void save(String email, String refreshToken, long validityMilliseconds) {
        redisTemplate.opsForValue()
                .set(PREFIX + email, refreshToken, validityMilliseconds, TimeUnit.MILLISECONDS);
    }

    //재발급 시 저장된 토큰과 일치하는지 확인
    public boolean isValid(String email, String refreshToken) {
        String stored = redisTemplate.opsForValue().get(PREFIX + email);
        return stored != null && stored.equals(refreshToken);
    }

    //로그아웃 시 저장된 토큰 삭제 → 이후로는 이 토큰으로 재발급 불가
    public void delete(String email) {
        redisTemplate.delete(PREFIX + email);
    }
}