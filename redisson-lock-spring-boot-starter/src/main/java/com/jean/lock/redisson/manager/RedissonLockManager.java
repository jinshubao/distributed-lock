package com.jean.lock.redisson.manager;

import com.jean.lock.manager.LockManager;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Redisson锁管理器
 *
 * @author jinshubao
 * @create 2018/05/15
 */
public class RedissonLockManager implements LockManager<RLock> {

    private static final Logger logger = LoggerFactory.getLogger(RedissonLockManager.class);

    private RedissonClient redissonClient;

    public RedissonLockManager(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public void unlock(RLock lock) {
        if (lock != null && lock.isLocked()) {
            long start = System.currentTimeMillis();
            lock.unlock();
            logger.debug("unlock [{}] {} ms", lock.getName(), (System.currentTimeMillis() - start));
        }
    }

    @Override
    public void unlock(String lockName) {
        this.unlock(redissonClient.getLock(lockName));
    }


    @Override
    public RLock getLock(String lockName) {
        Objects.requireNonNull(lockName);
        long start = System.currentTimeMillis();
        RLock lock = redissonClient.getLock(lockName);
        logger.debug("get lock [{}] {} ms", lockName, (System.currentTimeMillis() - start));
        return lock;
    }
}
