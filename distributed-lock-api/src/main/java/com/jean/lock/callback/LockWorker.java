package com.jean.lock.callback;

/**
 * 加锁后的业务逻辑
 *
 * @author jinshubao
 * @create 2018/05/15
 */
public interface LockWorker<T> {
    /**
     * 加锁后的业务方法
     *
     * @return
     * @throws Throwable
     */
    T work() throws Throwable;
}
