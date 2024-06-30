package com.commerce.couponcore.service;

import com.commerce.couponcore.component.DistributeLockExecutor;
import com.commerce.couponcore.exception.CouponIssueException;
import com.commerce.couponcore.exception.ErrorCode;
import com.commerce.couponcore.repository.redis.RedisRepository;
import com.commerce.couponcore.repository.redis.dto.CouponIssueRequest;
import com.commerce.couponcore.repository.redis.dto.CouponRedisEntity;
import com.commerce.couponcore.util.CouponRedisUtils;
import com.commerce.couponcore.util.RedisLockUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AsyncCouponIssueService {

    private final DistributeLockExecutor distributeLockExecutor;
    private final CouponIssueRedisService couponIssueRedisService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RedisRepository redisRepository;
    private final CouponCacheService couponCacheService;

    public void issueWithRedisLock(long couponId, long userId) {
        CouponRedisEntity coupon = couponCacheService.getCouponCache(couponId);
        coupon.checkIssuable(); // 날짜 + 쿠폰 전체 발행 갯수
        distributeLockExecutor.execute(RedisLockUtils.getCouponLockName(couponId), 3000, 3000, () -> {
            couponIssueRedisService.checkIssuable(coupon, userId); // 사용자 중복 발급 + max 발행 갯수 제한
            issueRequest(couponId, userId);
        });
    }

    public void issueWithScript(long couponId, long userId) {
        CouponRedisEntity coupon = couponCacheService.getCouponCache(couponId);
        coupon.checkIssuable(); // 날짜 + 쿠폰 전체 발행 갯수
        couponIssueRedisService.checkIssuable(coupon, userId);
        redisRepository.issueRequest(couponId, userId, coupon.totalQuantity());
    }

    public void issueWithScriptAndLocalCache(long couponId, long userId) {
        CouponRedisEntity coupon = couponCacheService.getCouponLocalCache(couponId);
        coupon.checkIssuable(); // 날짜 + 쿠폰 전체 발행 갯수
        couponIssueRedisService.checkIssuable(coupon, userId);
        redisRepository.issueRequest(couponId, userId, coupon.totalQuantity());
    }

    private void issueRequest(long couponId, long userId) {
        CouponIssueRequest issueRequest = new CouponIssueRequest(couponId, userId);
        try {
            String queueValue = objectMapper.writeValueAsString(issueRequest);
            redisRepository.sAdd(CouponRedisUtils.getCouponIssueRequestKey(couponId), String.valueOf(userId));
            redisRepository.rPush(CouponRedisUtils.getCouponIssueRequestQueueKey(), queueValue);
        } catch (JsonProcessingException e) {
            throw new CouponIssueException(ErrorCode.FAIL_COUPON_ISSUE_REQUEST);
        }
    }
}
