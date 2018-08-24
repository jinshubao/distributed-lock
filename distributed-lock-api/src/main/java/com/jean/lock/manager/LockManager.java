package com.jean.lock.manager;

import java.util.concurrent.locks.Lock;

/**
 * 锁管理器
 *
 * @author jinshubao
 * @create 2018/05/15
 */
public interface LockManager<T extends Lock> {

    /**
     * 解锁
     *
     * @param lock
     */
    void unlock(T lock);

    /**
     * 解锁
     *
     * @param lockName
     */
    void unlock(String lockName);

    /**
     * 获取锁
     *
     * @param lockName
     * @return
     */
    T getLock(String lockName);


}
