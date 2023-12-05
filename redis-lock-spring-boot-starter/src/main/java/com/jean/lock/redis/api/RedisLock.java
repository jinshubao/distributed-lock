package com.jean.lock.redis.api;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * TODO 少年，写点啥吧！
 *
 * @author jinshubao
 * @date 2018/06/07
 */
public class RedisLock implements Lock {

    private final String name;
    private final String key;
    private final Object value;

    public RedisLock(String name, String key, Object value) {
        this.name = name;
        this.key = key;
        this.value = value;
    }

    @Override
    public void lock() {

    }

    public void lock(long time, TimeUnit unit) throws InterruptedException {

    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }


    public boolean tryLock(long waitTime, long timeout, TimeUnit timeUnit) throws InterruptedException {
        return false;
    }


    @Override
    public void unlock() {

    }


    @Override
    public Condition newCondition() {
        return null;
    }

    public String getName() {
        return name;
    }


    public String getKey() {
        return key;
    }


    public Object getValue() {
        return value;
    }

}
