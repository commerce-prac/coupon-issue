package com.commerce.couponcore.service;

import com.commerce.couponcore.TestConfig;
import com.commerce.couponcore.exception.CouponIssueException;
import com.commerce.couponcore.exception.ErrorCode;
import com.commerce.couponcore.model.Coupon;
import com.commerce.couponcore.model.CouponIssue;
import com.commerce.couponcore.model.CouponType;
import com.commerce.couponcore.repository.CouponIssueJpaRepository;
import com.commerce.couponcore.repository.CouponIssueRepository;
import com.commerce.couponcore.repository.CouponJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

class CouponIssueServiceTest extends TestConfig {

    @Autowired
    private CouponIssueService sut;

    @Autowired
    private CouponIssueJpaRepository couponIssueJpaRepository;

    @Autowired
    private CouponJpaRepository couponJpaRepository;

    @Autowired
    private CouponIssueRepository couponIssueRepository;

    @BeforeEach
    void clean() {
        couponIssueJpaRepository.deleteAllInBatch();
        couponJpaRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("쿠폰 발급 기록 저장 1 : 쿠폰 발급 내역이 있으면, 예외를 반환한다.")
    void saveCouponIssue_1() {
        // given
        CouponIssue couponIssue = CouponIssue.builder()
                .couponId(1L)
                .userId(1L)
                .build();
        couponIssueJpaRepository.save(couponIssue);

        // when, then
        CouponIssueException exception = assertThrows(CouponIssueException.class, () -> sut.saveCouponIssue(1L, 1L));
        Assertions.assertEquals(exception.getErrorCode(), ErrorCode.COUPON_DUPLICATE);
    }

    @Test
    @DisplayName("쿠폰 발급 기록 저장 2 : 쿠폰 발급 내역이 없으면, 정상 발급된다.")
    void saveCouponIssue_2() {
        // given
        long couponId = 1L;
        long userId = 1L;

        // when, then
        CouponIssue result = sut.saveCouponIssue(couponId, userId);
        Assertions.assertTrue(couponIssueJpaRepository.findById(result.getId()).isPresent());
    }

    @Test
    @DisplayName("쿠폰 발급 1 : 발급 수량, 기한에 문제 없고, 쿠폰이 존재할 때 정상 발행")
    void issue_1() {
        // given
        long userId = 1L;
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .title("선착순 발급 쿠폰")
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .build();
        Coupon saved = couponJpaRepository.save(coupon);

        // when
        sut.issueWithRedisLock(coupon.getId(), userId);

        // then
        Assertions.assertEquals(saved.getIssuedQuantity(), 1);
        Assertions.assertNotNull(couponIssueRepository.findFirstCouponIssue(saved.getId(), userId));
    }

    @Test
    @DisplayName("쿠폰 발급 2 : 쿠폰이 존재하지 않을 때 예외 발생")
    void issue_2() {
        // given
        long userId = 1L;
        long couponId = 1L;

        // when & then
        CouponIssueException exception = assertThrows(CouponIssueException.class, () -> sut.issueWithRedisLock(couponId, userId));
        Assertions.assertEquals(exception.getErrorCode(), ErrorCode.COUPON_NOT_EXIST);
    }

    @Test
    @DisplayName("쿠폰 발급 3 : 발급 기한 만료 시, 예외 발생")
    void issue_3() {
        // given
        long userId = 1L;
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().minusDays(2))
                .dateIssueEnd(LocalDateTime.now().minusDays(1))
                .title("선착순 발급 쿠폰")
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .build();
        Coupon saved = couponJpaRepository.save(coupon);

        // when & then
        CouponIssueException exception = assertThrows(CouponIssueException.class, () -> sut.issueWithRedisLock(saved.getId(), userId));
        Assertions.assertEquals(exception.getErrorCode(), ErrorCode.COUPON_ISSUE_INVALID_DATE);
    }

    @Test
    @DisplayName("쿠폰 발급 4 : 발급 수량이 끝났을 때 예외 발생")
    void issue_4() {
        // given
        long userId = 1L;
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(100)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .title("선착순 발급 쿠폰")
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .build();
        Coupon saved = couponJpaRepository.save(coupon);

        // when & then
        CouponIssueException exception = assertThrows(CouponIssueException.class, () -> sut.issueWithRedisLock(saved.getId(), userId));
        Assertions.assertEquals(exception.getErrorCode(), ErrorCode.COUPON_ISSUE_INVALID_QUANTITY);
    }
}