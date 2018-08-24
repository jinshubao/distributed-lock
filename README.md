# 分布式锁

### 基于redisson的分布式锁
[基于redisson的分布式锁(已实现)](./redisson-lock-spring-boot-starter/README.md)


### 基于redis的分布式锁
[基于redis的分布式锁(未实现)](./redis-lock-spring-boot-starter/README.md)


### 基于zookeeper的分布式锁
[基于zookeeper的分布式锁(未实现)](./zookeeper-lock-spring-boot-starter/README.md)

### 使用方式

#### 配置
```
spring:
  profiles:
    active: dev
  application:
    name: order-service
  redis:
    host: 127.0.0.1
    port: 6379
    database: 0
    password: 123456
    pool:
      min-idle: 1 #min-idle必须大于0
      max-active: 8
      max-idle: 8
      max-wait: 1000
```

##### 使用方式

```
public class OrderServiceImpl {

    @Override
    @Transactional
    @DistributedLock(name = "buy_product", nameSuffix = "#param.productId+'_'+#param.userId")
    //@DistributedLock(name = "buy_product", namePrefix = "lock", nameSuffix = "#param.productId+'_'+#param.userId", waitTime = 3L, timeout = 30L, timeUnit = TimeUnit.SECONDS, fallback = "buyProductFallback")
    public Order buyProduct(Object param) {
        //冻结用户账户余额
        //扣减商品库存
        //TODO
        return new Order()
    }
    
    public Order buyProductFallback(Object param) {
            //TODO 加锁失败
    }
}
```

##### 注解 @DistributedLock 参数

- name 锁的名称
- namePrefix 锁的名称前缀，支持SpEL表达式。默认值`"lock"`
- nameSuffix 锁的名称后缀，可以设置与业务相关的值，支持SpEL表达式。默认值`""`
- waitTime 尝试加锁最长等待时间。waitTime等于0时，加锁失败不等待。当waitTime小于0时，会一直等待锁。默认值`3`
- timeout 锁超时时间。锁超时后自动释放。建议尽量缩简需要加锁的逻辑。timeout等于0时不加锁。默认值`30`
- timeUnit 时间单位。默认值`秒`
- fallback 加锁失败回调方法，该回调方法的返回值类型和参数必须与当前被加锁的方法一致。默认值`""`


> 锁在事务的外层执行，与注解的顺序无关，即：先加锁，再进入事务。
>
> redisson锁暂时不支持集群模式。