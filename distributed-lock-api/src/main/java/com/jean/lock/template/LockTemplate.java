package com.jean.lock.template;

import com.jean.lock.callback.LockWorker;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * 分布式锁模板类
 *
 * @author jinshubao
 * @create 2018/05/14
 */
public interface LockTemplate<T extends Lock> {


    /**
     * 加锁并执行业务逻辑
     *
     * @param lockName   锁名称
     * @param timeout    锁超时时间，超时后自动释放锁。
     * @param timeUnit   时间单位
     * @param lockWorker 业务
     * @param <R>        业务返回类型
     * @return 业务逻辑结果
     * @throws com.jean.lock.exception.UnableToAcquireLockException 无法加锁
     * @throws Throwable                                                   业务逻辑异常
     */
    <R> R lock(String lockName, long timeout, TimeUnit timeUnit, LockWorker<R> lockWorker) throws Throwable;

    /**
     * 加锁并执行业务逻辑
     *
     * @param lock       锁对象
     * @param timeout    锁超时时间，超时后自动释放锁。
     * @param timeUnit   时间单位
     * @param lockWorker 业务
     * @return 业务逻辑结果
     * @throws com.jean.lock.exception.UnableToAcquireLockException 无法加锁
     * @throws Throwable                                                   业务逻辑异常
     */
    <R> R lock(T lock, long timeout, TimeUnit timeUnit, LockWorker<R> lockWorker) throws Throwable;

    /**
     * 尝试加锁，加锁成功后执行业务逻辑
     *
     * @param lockName   锁名称
     * @param waitTime   加锁等待时间
     * @param timeout    锁超时时间，超时后自动释放锁。
     * @param timeUnit   时间单位
     * @param lockWorker 业务
     * @param <R>        业务返回类型
     * @return 业务逻辑结果
     * @throws com.jean.lock.exception.UnableToAcquireLockException 无法加锁
     * @throws Throwable                                                   业务逻辑异常
     */
    <R> R tryLock(String lockName, long waitTime, long timeout, TimeUnit timeUnit, LockWorker<R> lockWorker) throws Throwable;

    /**
     * 尝试加锁，加锁成功后执行业务逻辑
     *
     * @param lock       锁对象
     * @param waitTime   获取锁最长等待时间
     * @param timeout    锁超时时间。超时后自动释放锁。
     * @param timeUnit   时间单位
     * @param lockWorker 业务
     * @return 业务逻辑结果
     * @throws com.jean.lock.exception.UnableToAcquireLockException 无法加锁
     * @throws Throwable                                                   业务逻辑异常
     */
    <R> R tryLock(T lock, long waitTime, long timeout, TimeUnit timeUnit, LockWorker<R> lockWorker) throws Throwable;
}
