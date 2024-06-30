package com.commerce.couponcore.repository.redis.dto;

import com.commerce.couponcore.exception.CouponIssueException;
import com.commerce.couponcore.exception.ErrorCode;

public enum CouponIssueRequestCode {
    SUCCESS(1),
    DUPLICATED_COUPON_ISSUE(2),
    INVALID_COUPON_ISSUE_QUANTITY(3);


    CouponIssueRequestCode(int code) {
    }

    public static CouponIssueRequestCode find(String code) {
        int codeValue = Integer.parseInt(code);
        if (codeValue == 1) return SUCCESS;
        if (codeValue == 2) return DUPLICATED_COUPON_ISSUE;
        if (codeValue == 3) return INVALID_COUPON_ISSUE_QUANTITY;
        throw new IllegalArgumentException("CODE NUMBER NOT DEFINED : %s".formatted(code));
    }

    public static void checkRequestResult(CouponIssueRequestCode code) {
        if (code == INVALID_COUPON_ISSUE_QUANTITY) {
            throw new CouponIssueException(ErrorCode.COUPON_ISSUE_INVALID_QUANTITY);
        }
        if (code == DUPLICATED_COUPON_ISSUE) {
            throw new CouponIssueException(ErrorCode.COUPON_ALREADY_ISSUED);
        }
    }
}
