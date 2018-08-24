package com.jean.lock.redisson.aop;

import com.jean.lock.annotation.DistributedLock;
import com.jean.lock.exception.UnableToAcquireLockException;
import com.jean.lock.redisson.config.DistributedLockConfig;
import com.jean.lock.template.LockTemplate;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.Ordered;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * 锁注解处理
 * <p>
 * 事务注解的优先级是Ordered.LOWEST_PRECEDENCE
 *
 * @author jinshubao
 * @create 2018/05/14
 */
@Aspect
@ConditionalOnClass(LockTemplate.class)
@AutoConfigureAfter(DistributedLockConfig.class)
@Order(Ordered.LOWEST_PRECEDENCE - 1)
public class DistributedLockAspect {

    private static final String EXPRESSION_PREFIX = "#";

    private ExpressionParser parser = new SpelExpressionParser();

    private ParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();

    private LockTemplate<RLock> lockTemplate;

    @Autowired
    public DistributedLockAspect(LockTemplate<RLock> lockTemplate) {
        this.lockTemplate = lockTemplate;
    }

    @Pointcut(value = "@annotation(com.jean.lock.annotation.DistributedLock)")
    public void pointcut() {
    }

    @Around(value = "pointcut()&&@annotation(distributedLock)")
    public Object around(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {

        String prefix = distributedLock.namePrefix();
        String name = distributedLock.name();
        String suffix = distributedLock.nameSuffix();
        long waitTime = distributedLock.waitTime();
        long timeout = distributedLock.timeout();
        if (timeout <= 0) {
            timeout = -1;
        }
        TimeUnit timeUnit = distributedLock.timeUnit();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Object[] args = joinPoint.getArgs();
        Method method = signature.getMethod();
        if (!StringUtils.isEmpty(prefix) && prefix.contains(EXPRESSION_PREFIX)) {
            prefix = this.parse(prefix, method, args);
        }
        if (!StringUtils.isEmpty(name) && name.contains(EXPRESSION_PREFIX)) {
            name = this.parse(name, method, args);
        }
        if (!StringUtils.isEmpty(suffix) && suffix.contains(EXPRESSION_PREFIX)) {
            suffix = this.parse(suffix, method, args);
        }
        String lockName = StringUtils.arrayToDelimitedString(new String[]{prefix, name, suffix}, distributedLock.separator());

        try {
            if (waitTime < 0) {
                return lockTemplate.lock(lockName, timeout, timeUnit, joinPoint::proceed);
            } else {
                return lockTemplate.tryLock(lockName, waitTime, timeout, timeUnit, joinPoint::proceed);
            }
        } catch (UnableToAcquireLockException e) {
            String fallback = distributedLock.fallback();
            if (StringUtils.isEmpty(fallback)) {
                return null;
            }
            //加锁失败回调
            Object target = joinPoint.getTarget();
            Method fallbackMethod = ReflectionUtils.findMethod(target.getClass(), fallback, signature.getParameterTypes());
            if (fallbackMethod == null) {
                throw new RuntimeException("fallback method [" + fallback + "] not fund");
            }
            return ReflectionUtils.invokeMethod(fallbackMethod, target, args);
        }
    }


    /**
     * 解析SPL表达式
     *
     * @param expressionString
     * @param method
     * @param args
     * @return
     */
    private String parse(String expressionString, Method method, Object[] args) {
        String[] params = discoverer.getParameterNames(method);
        EvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < params.length; i++) {
            context.setVariable(params[i], args[i]);
        }
        return parser.parseExpression(expressionString).getValue(context, String.class);
    }
}
