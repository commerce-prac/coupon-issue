package com.commerce.couponapi.service;

import com.commerce.couponapi.controller.dto.CouponIssueRequestDto;
import com.commerce.couponcore.component.DistributeLockExecutor;
import com.commerce.couponcore.service.AsyncCouponIssueService;
import com.commerce.couponcore.service.CouponIssueService;
import com.commerce.couponcore.util.RedisLockUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponIssueRequestService {

    private final CouponIssueService couponIssueService;
    private final AsyncCouponIssueService asyncCouponIssueService;
    private final DistributeLockExecutor distributeLockExecutor;

    public void issueRequestWithRedisLock(CouponIssueRequestDto requestDto) {
        distributeLockExecutor.execute(RedisLockUtils.getCouponLockName(requestDto.couponId()),
                10000, 10000,
                () -> couponIssueService.issueWithRedisLock(requestDto.couponId(), requestDto.userId()));
        log.info("쿠폰 발급 완료. coupon id : {}, user id : {}", requestDto.couponId(), requestDto.userId());
    }

    public void issueRequestWithMySQLLock(CouponIssueRequestDto requestDto) {
        couponIssueService.issueWithMySQLLock(requestDto.couponId(), requestDto.userId());
        log.info("쿠폰 발급 완료. coupon id : {}, user id : {}", requestDto.couponId(), requestDto.userId());
    }

    public void asyncIssueRequestWithRedisLock(CouponIssueRequestDto requestDto) {
        asyncCouponIssueService.issueWithRedisLock(requestDto.couponId(), requestDto.userId());
    }

    public void asyncIssueRequestWithScript(CouponIssueRequestDto requestDto) {
        asyncCouponIssueService.issueWithScript(requestDto.couponId(), requestDto.userId());
    }
}
