package com.commerce.couponcore.service;

import com.commerce.couponcore.exception.CouponIssueException;
import com.commerce.couponcore.exception.ErrorCode;
import com.commerce.couponcore.model.Coupon;
import com.commerce.couponcore.repository.redis.RedisRepository;
import com.commerce.couponcore.util.CouponRedisUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponIssueRedisService {

    private final RedisRepository redisRepository;


    public void checkIssuable(Coupon coupon, Long userId) {
        if (!availableUserIssueQuantity(coupon.getId(), userId)) {
            throw new CouponIssueException(ErrorCode.COUPON_ALREADY_ISSUED);
        }
    }

    public boolean availableUserIssueQuantity(long couponId, long userId) {
        String key = CouponRedisUtils.getCouponIssueRequestKey(couponId);
        return !redisRepository.sIsMember(key, String.valueOf(userId));
    }
}