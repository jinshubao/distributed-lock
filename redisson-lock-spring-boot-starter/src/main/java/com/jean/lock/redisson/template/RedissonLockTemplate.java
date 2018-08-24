package com.jean.lock.redisson.template;

import com.jean.lock.callback.LockWorker;
import com.jean.lock.exception.UnableToAcquireLockException;
import com.jean.lock.manager.LockManager;
import com.jean.lock.template.LockTemplate;
import org.redisson.api.RLock;
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

    private LockManager<RLock> lockLockManager;

    public RedissonLockTemplate(LockManager<RLock> lockLockManager) {
        this.lockLockManager = lockLockManager;
    }


    @Override
    public <R> R lock(String lockName, long timeout, TimeUnit timeUnit, LockWorker<R> lockWorker) throws Throwable {
        RLock lock = lockLockManager.getLock(lockName);
        return lock(lock, timeout, timeUnit, lockWorker);
    }

    @Override
    public <R> R lock(RLock lock, long timeout, TimeUnit timeUnit, LockWorker<R> lockWorker) throws Throwable {
        long start = System.currentTimeMillis();
        lock.lock(timeout, timeUnit);
        try {
            logger.debug("lock success [{}] {} ms", lock.getName(), System.currentTimeMillis() - start);
            return lockWorker.work();
        } finally {
            lockLockManager.unlock(lock);
        }
    }

    @Override
    public <R> R tryLock(String lockName, long waitTime, long timeout, TimeUnit timeUnit, LockWorker<R> lockWorker) throws Throwable {
        RLock lock = lockLockManager.getLock(lockName);
        return tryLock(lock, waitTime, timeout, timeUnit, lockWorker);
    }

    @Override
    public <R> R tryLock(RLock lock, long waitTime, long timeout, TimeUnit timeUnit, LockWorker<R> lockWorker) throws Throwable {
        long start = System.currentTimeMillis();
        boolean locked = lock.tryLock(waitTime, timeout, timeUnit);
        if (locked) {
            try {
                logger.debug("try lock success [{}], {} ms", lock.getName(), (System.currentTimeMillis() - start));
                return lockWorker.work();
            } finally {
                lockLockManager.unlock(lock);
            }
        }
        throw new UnableToAcquireLockException("try lock failed [" + lock.getName() + "]");
    }
}
