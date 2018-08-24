package com.jean.lock.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁，可重入锁
 *
 * @author jinshubao
 * @create 2018/05/14
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface DistributedLock {

    /**
     * name前缀
     */
    String namePrefix() default "lock";

    /**
     * 锁的名称
     */
    String name();

    /**
     * name后缀
     */
    String nameSuffix() default "";

    /**
     * 获得锁名时拼接前后缀用到的分隔符
     */
    String separator() default ".";

    /**
     * <p>尝试加锁，最长等待时间。
     * <p>waitTime==0，不等待，加锁失败抛出{@link com.jean.lock.exception.UnableToAcquireLockException}异常。
     * <p>waitTime<0，一直等待锁。
     */
    long waitTime() default 3L;

    /**
     * 锁定超时时间。锁定时间超过timeout设置的时长后动释放锁自，建议尽量减少需要加锁的业务逻辑，缩短锁定时间。timeout<=0时不自动释放锁。
     */
    long timeout() default 30L;

    /**
     * 时间单位。默认为秒。
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 加锁失败回调方法，该回调方法的返回值类型和参数必须与当前被加锁的方法一致，例如：
     * <pre>
     * &#064;DistributedLock(name="test", nameSuffix="#str", fallback="testFallback")
     * public String test(String str, Integer id){
     *      //...
     * }
     *
     * public String testFallback(String str, Integer id){
     *      //fallback
     * }
     * </pre>
     * 主意：fallback配置为空字符串时不会回调，并且返回值为null
     *
     * @return
     */
    String fallback() default "";
}
