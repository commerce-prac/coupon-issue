package com.commerce.couponcore.component;

import com.commerce.couponcore.exception.ErrorCode;
import com.commerce.couponcore.exception.LockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Component
public class DistributeLockExecutor {
    private final RedissonClient redissonClient;

    public void execute(String lockName, long waitMilliSeconds, long releaseMilliSeconds, Runnable logic) {
        RLock lock = redissonClient.getLock(lockName);
        try {
            boolean isLocked = lock.tryLock(waitMilliSeconds, releaseMilliSeconds, TimeUnit.MILLISECONDS);
            if (!isLocked) {
                log.error("Lock 획득 실패, Lock Name : {}", lockName);
                throw new LockException(ErrorCode.LOCK_FAIL);
            }
            logic.run();
        } catch (InterruptedException exception) {
            log.error("Interrupted Exception - Lock 획득 실패, Lock Name : {}", lockName, exception.getMessage());
            throw new LockException(ErrorCode.LOCK_FAIL);
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
