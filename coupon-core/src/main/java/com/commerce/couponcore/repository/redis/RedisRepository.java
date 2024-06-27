package com.commerce.couponcore.repository.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public boolean sIsMember(String key, String value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    public void sAdd(String key, String value) {
        redisTemplate.opsForSet().add(key, value);
    }

    public void rPush(String key, String value) {
        redisTemplate.opsForList().rightPush(key, value);
    }
}
