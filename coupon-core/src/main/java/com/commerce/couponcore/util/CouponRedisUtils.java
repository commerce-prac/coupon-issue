package com.commerce.couponcore.util;

public class CouponRedisUtils {

    public static String getCouponIssueRequestKey(Long couponId) {
        return "request:issue:coupon:%s".formatted(couponId);
    }

    public static String getCouponIssueRequestQueueKey() {
        return "request:issue:coupon:queue";
    }
}
