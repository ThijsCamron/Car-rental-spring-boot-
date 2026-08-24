package com.rental.CarRental.service.security;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

@Service
public class TokenBlackListService {

    private static final String PREFIX = "revoked:";
    private final RedisTemplate<String, String> redisTemplate;

    public TokenBlackListService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void revoke(String jti, Duration expiration) {
        redisTemplate.opsForValue().set(
                PREFIX + jti,
                "1",
                expiration
        );
    }

    public boolean isRevoked(String jti) {
        return redisTemplate.hasKey(PREFIX + jti);
    }
}
