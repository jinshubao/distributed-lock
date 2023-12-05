package com.jean.lock.redis.template;

import com.jean.lock.callback.LockWorker;
import com.jean.lock.exception.UnableToAcquireLockException;
import com.jean.lock.redis.api.RedisLock;
import com.jean.lock.template.LockTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 基于redis的分布式锁模板类
 *
 * @author jinshubao
 * @create 2018/06/07
 */

public class RedisLockTemplate implements LockTemplate<RedisLock> {

    private static final Logger logger = LoggerFactory.getLogger(RedisLockTemplate.class);


    private static final RedisScript<String> LOCK_SCRIPT = new DefaultRedisScript<>("return redis.call('set',KEYS[1],ARGV[1],'NX','PX',ARGV[2])", String.class);
    private static final RedisScript<String> UNLOCK_SCRIPT = new DefaultRedisScript<>("if redis.call('get',KEYS[1]) == ARGV[1] then return tostring(redis.call('del', KEYS[1])==1) else return 'false' end", String.class);
    private static final RedisScript<Boolean> RENEWAL_SCRIPT = new DefaultRedisScript<>("if redis.call('get', KEYS[1]) ==ARGV[1] then return redis.call('pexpire', KEYS[1], ARGV[2]) else  return 0  end", Boolean.class);
    private static final String LOCK_SUCCESS = "OK";


    private final RedisTemplate<String, Object> redisTemplate;

    public RedisLockTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    @Override
    public <R> R lock(String lockName, long timeout, TimeUnit timeUnit, LockWorker<R> lockWorker) {
        RedisLock lock = getLock(lockName, timeout, timeout);
        try {
            lock.lock(timeout, timeUnit);
        } catch (InterruptedException e) {
            throw new UnableToAcquireLockException("try lock failed [" + lock.getName() + "]");
        }
        try {
            return lockWorker.work();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public <R> R tryLock(String lockName, long waitTime, long timeout, TimeUnit timeUnit, LockWorker<R> lockWorker) {
        RedisLock lock = tryGetLock(lockName, waitTime, timeout, timeUnit);
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
            lock.unlock();
        }
    }

    private RedisLock tryGetLock(String lockName, long waitTime, long timeout, TimeUnit timeUnit) {
        return null;
    }

    RedisLock getLock(String lockName, long timeout, long l) {
        /*
        CompletableFuture<String> cf = CompletableFuture.supplyAsync(() -> redisTemplate.execute(SCRIPT_LOCK,
                        redisTemplate.getStringSerializer(),
                        redisTemplate.getStringSerializer(),
                        Collections.singletonList(lockName),
                        lockName, String.valueOf(timeout)))
                .thenApply(acquired -> {
                    //成功且传-1时开始续期
                    if (LOCK_SUCCESS.equals(acquired) && timeout == -1) {
//                        renewExpiration(expire, lockKey, lockValue);

                        //TODO 自动续期
                    }
                    return acquired;
                });
        String lock;
        try {
            lock = cf.get();
            final boolean locked = LOCK_SUCCESS.equals(lock);
        } catch (ExecutionException | InterruptedException e) {
            throw new UnableToAcquireLockException(e);
        }
        */

        // TODO
        return null;
    }
}
