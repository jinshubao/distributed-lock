package com.jean.lock.redis.manager;

import com.jean.lock.manager.LockManager;
import com.jean.lock.redis.api.RedisLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.ArrayList;
import java.util.List;

/**
 * Redis锁管理器
 *
 * @author jinshubao
 * @create 2018/06/07
 */
public class RedisLockManager implements LockManager<RedisLock> {

    private static final Logger logger = LoggerFactory.getLogger(RedisLockManager.class);

    private RedisTemplate<String, Object> redisTemplate;

    public RedisLockManager(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void unlock(RedisLock lock) {

    }

    @Override
    public void unlock(String lockName) {

    }

    @Override
    public RedisLock getLock(String lockName) {
        List<String> list = new ArrayList<>();
        Integer execute = redisTemplate.execute(new DefaultRedisScript<>("", Integer.class), list, "", "");

        //TODO
        return null;
    }
}
