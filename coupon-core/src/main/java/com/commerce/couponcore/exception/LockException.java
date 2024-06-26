package com.commerce.couponcore.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LockException extends RuntimeException {

    private final ErrorCode errorCode;

}
