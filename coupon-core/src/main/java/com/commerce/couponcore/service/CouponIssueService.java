package com.commerce.couponcore.service;

import com.commerce.couponcore.event.CouponIssueCompleteEvent;
import com.commerce.couponcore.exception.CouponIssueException;
import com.commerce.couponcore.exception.ErrorCode;
import com.commerce.couponcore.model.Coupon;
import com.commerce.couponcore.model.CouponIssue;
import com.commerce.couponcore.repository.CouponIssueJpaRepository;
import com.commerce.couponcore.repository.CouponIssueRepository;
import com.commerce.couponcore.repository.CouponJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class CouponIssueService {

    private final CouponJpaRepository couponJpaRepository;
    private final CouponIssueJpaRepository couponIssueJpaRepository;
    private final CouponIssueRepository couponIssueRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public void issueWithRedisLock(long couponId, long userId) {
        Coupon coupon = findCoupon(couponId);
        coupon.issue();
        saveCouponIssue(couponId, userId);
    }

    @Transactional
    public void issueWithMySQLLock(long couponId, long userId) {
        Coupon coupon = findCouponWithLock(couponId);
        coupon.issue();
        saveCouponIssue(couponId, userId);
    }

    @Transactional
    public void issueFromQueue(long couponId, long userId) {
        Coupon coupon = findCoupon(couponId);
        coupon.issue();
        saveCouponIssue(couponId, userId);
        publishEvent(new CouponIssueCompleteEvent(couponId));
    }

    @Transactional
    public CouponIssue saveCouponIssue(long couponId, long userId) {
        checkAlreadyIssued(couponId, userId);
        CouponIssue couponIssue = CouponIssue.builder()
                .couponId(couponId)
                .userId(userId)
                .build();
        return couponIssueJpaRepository.save(couponIssue);
    }

    @Transactional(readOnly = true)
    public void checkAlreadyIssued(long couponId, long userId) {
        CouponIssue couponIssue = couponIssueRepository.findFirstCouponIssue(couponId, userId);
        if (couponIssue != null) {
            throw new CouponIssueException(ErrorCode.COUPON_ALREADY_ISSUED);
        }
    }

    @Transactional(readOnly = true)
    public Coupon findCoupon(long couponId) {
        return couponJpaRepository.findById(couponId)
                .orElseThrow(() -> new CouponIssueException(ErrorCode.COUPON_NOT_EXIST));
    }

    @Transactional(readOnly = true)
    public Coupon findCouponWithLock(long couponId) {
        return couponJpaRepository.findCouponWithLock(couponId)
                .orElseThrow(() -> new CouponIssueException(ErrorCode.COUPON_NOT_EXIST));
    }

    private void publishEvent(CouponIssueCompleteEvent event) {
        applicationEventPublisher.publishEvent(event);
    }

}
