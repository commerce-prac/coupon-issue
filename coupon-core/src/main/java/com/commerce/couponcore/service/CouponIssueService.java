package com.commerce.couponcore.service;

import com.commerce.couponcore.exception.CouponIssueException;
import com.commerce.couponcore.exception.ErrorCode;
import com.commerce.couponcore.model.Coupon;
import com.commerce.couponcore.model.CouponIssue;
import com.commerce.couponcore.repository.CouponIssueJpaRepository;
import com.commerce.couponcore.repository.CouponIssueRepository;
import com.commerce.couponcore.repository.CouponJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CouponIssueService {

    private final CouponJpaRepository couponJpaRepository;
    private final CouponIssueJpaRepository couponIssueJpaRepository;
    private final CouponIssueRepository couponIssueRepository;

    @Transactional
    public void issue(long couponId, long userId) {
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
            throw new CouponIssueException(ErrorCode.COUPON_DUPLICATE);
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
}
