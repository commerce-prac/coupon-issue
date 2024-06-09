package com.commerce.couponcore.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CouponIssueException extends RuntimeException {
    private final ErrorCode errorCode;
}
