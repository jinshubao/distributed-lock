package com.jean.lock.exception;

/**
 * 获取锁失败异常
 *
 * @author jinshubao
 * @create 2018/05/15
 */
public class UnableToAcquireLockException extends RuntimeException {
    public UnableToAcquireLockException() {
    }

    public UnableToAcquireLockException(String message) {
        super(message);
    }

    public UnableToAcquireLockException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnableToAcquireLockException(Throwable cause) {
        super(cause);
    }

    public UnableToAcquireLockException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
