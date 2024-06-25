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
    // 404 NOT_FOUND
    COUPON_NOT_EXIST(HttpStatus.NOT_FOUND, "001", "Coupon not exist"),
    // 409 CONFLICT
    COUPON_DUPLICATE(HttpStatus.CONFLICT, "001", "Coupon already issued"),
    // 500 INTERNAL_SERVER_ERROR
    LOCK_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "001", "Lock failed");

    private final HttpStatus httpStatus;
    private final String code;
    private final String reason;
}
