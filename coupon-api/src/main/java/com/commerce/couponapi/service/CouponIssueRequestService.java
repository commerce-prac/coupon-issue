package com.commerce.couponapi.service;

import com.commerce.couponapi.controller.dto.CouponIssueRequestDto;
import com.commerce.couponcore.component.DistributeLockExecutor;
import com.commerce.couponcore.service.CouponIssueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponIssueRequestService {

    private final CouponIssueService couponIssueService;
    private final DistributeLockExecutor distributeLockExecutor;

    public void issueRequestWithRedisLock(CouponIssueRequestDto requestDto) {
        distributeLockExecutor.execute("lock_" + requestDto.couponId(),
                10000, 10000,
                () -> couponIssueService.issue(requestDto.couponId(), requestDto.userId()));
        log.info("쿠폰 발급 완료. coupon id : {}, user id : {}", requestDto.couponId(), requestDto.userId());
    }

    public void issueRequestWithMySQLLock(CouponIssueRequestDto requestDto) {
        couponIssueService.issueWithMySQLLock(requestDto.couponId(), requestDto.userId());
        log.info("쿠폰 발급 완료. coupon id : {}, user id : {}", requestDto.couponId(), requestDto.userId());
    }
}
