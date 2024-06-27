package com.commerce.couponcore.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // 400 BAD_REQUEST
    COUPON_ISSUE_INVALID_QUANTITY(HttpStatus.BAD_REQUEST, "001", "Invalid quantity"),
    COUPON_ISSUE_INVALID_DATE(HttpStatus.BAD_REQUEST, "002", "Invalid date"),
    COUPON_ALREADY_ISSUED(HttpStatus.BAD_REQUEST, "003", "Already issued"),
    // 404 NOT_FOUND
    COUPON_NOT_EXIST(HttpStatus.NOT_FOUND, "001", "Coupon not exist"),
    // 409 CONFLICT
    COUPON_DUPLICATE(HttpStatus.CONFLICT, "001", "Coupon already issued"),
    // 500 INTERNAL_SERVER_ERROR
    LOCK_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "001", "Lock failed"),
    FAIL_COUPON_ISSUE_REQUEST(HttpStatus.INTERNAL_SERVER_ERROR, "002", "Coupon issue request failed."),
    FAILED_PULL_COUPON_ISSUE_REQUEST(HttpStatus.INTERNAL_SERVER_ERROR, "003", "Coupon issue request pulling failed.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String reason;
}
