package com.commerce.couponcore.service;

import com.commerce.couponcore.exception.CouponIssueException;
import com.commerce.couponcore.exception.ErrorCode;
import com.commerce.couponcore.repository.redis.RedisRepository;
import com.commerce.couponcore.repository.redis.dto.CouponRedisEntity;
import com.commerce.couponcore.util.CouponRedisUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponIssueRedisService {

    private final RedisRepository redisRepository;


    public void checkIssuable(CouponRedisEntity coupon, Long userId) {
        if (!availableUserIssueQuantity(coupon.id(), userId)) {
            throw new CouponIssueException(ErrorCode.COUPON_ALREADY_ISSUED);
        }
        if (!availableCouponTotalQuantity(coupon.id(), coupon.totalQuantity())) {
            throw new CouponIssueException(ErrorCode.COUPON_ISSUE_INVALID_QUANTITY);
        }
    }

    public boolean availableUserIssueQuantity(long couponId, long userId) {
        String key = CouponRedisUtils.getCouponIssueRequestKey(couponId);
        return !redisRepository.sIsMember(key, String.valueOf(userId));
    }

    public boolean availableCouponTotalQuantity(long couponId, long totalQuantity) {
        String key = CouponRedisUtils.getCouponIssueRequestKey(couponId);
        return redisRepository.setSize(key) < totalQuantity;
    }
}
