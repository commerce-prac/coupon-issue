package com.commerce.couponcore.service;

import com.commerce.couponcore.component.DistributeLockExecutor;
import com.commerce.couponcore.exception.CouponIssueException;
import com.commerce.couponcore.exception.ErrorCode;
import com.commerce.couponcore.model.Coupon;
import com.commerce.couponcore.repository.redis.RedisRepository;
import com.commerce.couponcore.repository.redis.dto.CouponIssueRequest;
import com.commerce.couponcore.util.CouponRedisUtils;
import com.commerce.couponcore.util.RedisLockUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AsyncCouponIssueService {

    private final CouponIssueService couponIssueService;
    private final DistributeLockExecutor distributeLockExecutor;
    private final CouponIssueRedisService couponIssueRedisService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RedisRepository redisRepository;

    public void issue(long couponId, long userId) {
        Coupon coupon = couponIssueService.findCoupon(couponId);
        coupon.checkIssuable();
        distributeLockExecutor.execute(RedisLockUtils.getCouponLockName(couponId), 3000, 3000, () -> {
            couponIssueRedisService.checkIssuable(coupon, userId);
            issueRequest(couponId, userId);
        });
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
