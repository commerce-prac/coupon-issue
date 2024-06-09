package com.commerce.couponcore.model;

import com.commerce.couponcore.exception.CouponIssueException;
import com.commerce.couponcore.exception.ErrorCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CouponTest {

    @Test
    @DisplayName("수량 검증 1 : 발급 가능한 쿠폰 수량이 남아있을 때 true를 반환한다.")
    void availableIssueQuantity_1() {
        // given
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(99)
                .build();
        // when
        var result = coupon.availableIssueQuantity();
        // then
        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("수량 검증 2 : 발급 가능한 쿠폰 수량이 없을 때 false를 반환한다.")
    void availableIssueQuantity_2() {
        // given
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(100)
                .build();
        // when
        var result = coupon.availableIssueQuantity();
        // then
        Assertions.assertFalse(result);
    }

    @Test
    @DisplayName("수량 검증 3 : 발급 가능한 수량이 정해지지 않았을 때, 항상 true를 반환한다.")
    void availableIssueQuantity_3() {
        // given
        Coupon coupon = Coupon.builder()
                .totalQuantity(null)
                .build();
        // when
        var result = coupon.availableIssueQuantity();
        // then
        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("기한 검증 1 : 기한 내에 있다면, true를 반환한다.")
    void availableIssueDate_1() {
        // given
        Coupon coupon = Coupon.builder()
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();
        // when
        var result = coupon.availableIssueDate();
        // then
        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("기한 검증 2 : 지금 시작 전이라면, false를 반환한다.")
    void availableIssueDate_2() {
        // given
        Coupon coupon = Coupon.builder()
                .dateIssueStart(LocalDateTime.now().plusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(2))
                .build();
        // when
        var result = coupon.availableIssueDate();
        // then
        Assertions.assertFalse(result);
    }

    @Test
    @DisplayName("기한 검증 3 : 지금 이미 끝났다면, false를 반환한다.")
    void availableIssueDate_3() {
        // given
        Coupon coupon = Coupon.builder()
                .dateIssueStart(LocalDateTime.now().minusDays(10))
                .dateIssueEnd(LocalDateTime.now().minusDays(5))
                .build();
        // when
        var result = coupon.availableIssueDate();
        // then
        Assertions.assertFalse(result);
    }

    @Test
    @DisplayName("쿠폰 발행 1 : 수량과 기한이 모두 가능하면 쿠폰이 발행된다.")
    void issue_1() {
        // given
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(99)
                .dateIssueStart(LocalDateTime.now().minusDays(2))
                .dateIssueEnd(LocalDateTime.now().plusDays(2))
                .build();
        // when
        coupon.issue();
        // then
        Assertions.assertEquals(coupon.getIssuedQuantity(), 100);
    }

    @Test
    @DisplayName("쿠폰 발행 2 : 이미 발급가능한 수량을 초과했을 경우, 수량 초과 에러가 발생한다.")
    void issue_2() {
        // given
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(100)
                .dateIssueStart(LocalDateTime.now().minusDays(2))
                .dateIssueEnd(LocalDateTime.now().plusDays(2))
                .build();
        // when, then
        CouponIssueException exception = assertThrows(CouponIssueException.class, coupon::issue);
        Assertions.assertEquals(exception.getErrorCode(), ErrorCode.COUPON_ISSUE_INVALID_QUANTITY);
    }

    @Test
    @DisplayName("쿠폰 발행 3 : 이미 기한 종료된 경우, 발급 기한 에러가 발생한다.")
    void issue_3() {
        // given
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(99)
                .dateIssueStart(LocalDateTime.now().minusDays(10))
                .dateIssueEnd(LocalDateTime.now().minusDays(5))
                .build();
        // when, then
        CouponIssueException exception = assertThrows(CouponIssueException.class, coupon::issue);
        Assertions.assertEquals(exception.getErrorCode(), ErrorCode.COUPON_ISSUE_INVALID_DATE);
    }
}