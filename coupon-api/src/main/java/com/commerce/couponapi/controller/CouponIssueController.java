package com.commerce.couponapi.controller;

import com.commerce.couponapi.controller.dto.CouponIssueRequestDto;
import com.commerce.couponapi.controller.dto.CouponIssueResponseDto;
import com.commerce.couponapi.service.CouponIssueRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class CouponIssueController {

    private final CouponIssueRequestService couponIssueRequestService;

    @PostMapping("/issue/lock/redis")
    public CouponIssueResponseDto issueRedis(@RequestBody CouponIssueRequestDto requestDto) {
        couponIssueRequestService.issueRequestWithRedisLock(requestDto);
        return new CouponIssueResponseDto(true, null);
    }

    @PostMapping("/issue/lock/mysql")
    public CouponIssueResponseDto issueMySQL(@RequestBody CouponIssueRequestDto requestDto) {
        couponIssueRequestService.issueRequestWithMySQLLock(requestDto);
        return new CouponIssueResponseDto(true, null);
    }
}
