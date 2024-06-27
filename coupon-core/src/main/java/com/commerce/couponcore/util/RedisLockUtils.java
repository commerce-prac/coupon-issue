package com.commerce.couponcore.util;

public class RedisLockUtils {
    public static String getCouponLockName(Long couponId) {
        return "lock:issue:coupon:%s".formatted(couponId);
    }
}
