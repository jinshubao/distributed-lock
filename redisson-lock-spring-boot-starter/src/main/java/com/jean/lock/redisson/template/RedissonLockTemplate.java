package com.jean.lock.redisson.template;

import com.jean.lock.callback.LockWorker;
import com.jean.lock.exception.UnableToAcquireLockException;
import com.jean.lock.template.LockTemplate;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 基于redisson的分布式锁模板类
 *
 * @author jinshubao
 * @create 2018/05/14
 */

public class RedissonLockTemplate implements LockTemplate<RLock> {

    private static final Logger logger = LoggerFactory.getLogger(RedissonLockTemplate.class);


    private RedissonClient redissonClient;

    public RedissonLockTemplate(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }


    @Override
    public <R> R lock(String lockName, long timeout, TimeUnit timeUnit, LockWorker<R> lockWorker) {
        RLock lock = redissonClient.getLock(lockName);
        try {
            lock.lock(timeout, timeUnit);
            return lockWorker.work();
        } finally {
            if (lock.isLocked()) {
                lock.unlock();
            }
        }
    }

    @Override
    public <R> R tryLock(String lockName, long waitTime, long timeout, TimeUnit timeUnit, LockWorker<R> lockWorker) {
        RLock lock = redissonClient.getLock(lockName);
        boolean locked;
        try {
            locked = lock.tryLock(waitTime, timeout, timeUnit);
        } catch (InterruptedException e) {
            throw new UnableToAcquireLockException(e);
        }
        if (!locked) {
            throw new UnableToAcquireLockException("try lock failed [" + lock.getName() + "]");
        }
        try {
            return lockWorker.work();
        } finally {
            if (lock.isLocked()) {
                lock.unlock();
            }
        }
    }
}
