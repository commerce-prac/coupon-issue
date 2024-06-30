package com.commerce.couponconsumer.listener;

import com.commerce.couponcore.exception.CouponIssueException;
import com.commerce.couponcore.exception.ErrorCode;
import com.commerce.couponcore.repository.redis.RedisRepository;
import com.commerce.couponcore.repository.redis.dto.CouponIssueRequest;
import com.commerce.couponcore.service.CouponIssueService;
import com.commerce.couponcore.util.CouponRedisUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@EnableScheduling
@RequiredArgsConstructor
public class CouponIssueListener {

    private final CouponIssueService couponIssueService;
    private final RedisRepository redisRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String issueRequestQueueKey = CouponRedisUtils.getCouponIssueRequestQueueKey();

    @Scheduled(fixedDelay = 1000)
    public void issue() {
        var queueSize = getCouponIssueRequestSize();
        while (queueSize > 0) {
            var target = getIssueTarget();
            removeIssuedTarget();
            queueSize--;
            log.info("쿠폰 발행 시작 coupon id : {}, user id : {}", target.couponId(), target.userId());
            couponIssueService.issueFromQueue(target.couponId(), target.userId());
            log.info("쿠폰 발행 완료. coupon id : {}, user id : {}", target.couponId(), target.userId());
        }
    }

    private CouponIssueRequest getIssueTarget() {
        CouponIssueRequest req;
        try {
            req = objectMapper.readValue(redisRepository.listIndex(issueRequestQueueKey, 0), CouponIssueRequest.class);
        } catch (JsonProcessingException exception) {
            throw new CouponIssueException(ErrorCode.FAILED_PULL_COUPON_ISSUE_REQUEST);
        }
        return req;
    }

    private Long getCouponIssueRequestSize() {
        return redisRepository.listSize(issueRequestQueueKey);
    }

    private void removeIssuedTarget() {
        redisRepository.lPop(issueRequestQueueKey);
    }
}
