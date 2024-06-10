package com.commerce.couponapi.service;

import com.commerce.couponapi.controller.dto.CouponIssueRequestDto;
import com.commerce.couponcore.service.CouponIssueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponIssueRequestService {

    private final CouponIssueService couponIssueService;

    public void issueRequest(CouponIssueRequestDto requestDto) {
        couponIssueService.issue(requestDto.couponId(), requestDto.userId());
        log.info("쿠폰 발급 완료. coupon id : {}, user id : {}", requestDto.couponId(), requestDto.userId());
    }
}
