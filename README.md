# 分布式锁

### 基于redisson的分布式锁
[基于redisson的分布式锁(已实现)](./redisson-lock-spring-boot-starter/README.md)


### 基于redis的分布式锁
[基于redis的分布式锁(未实现)](./redis-lock-spring-boot-starter/README.md)


### 基于zookeeper的分布式锁
[基于zookeeper的分布式锁(未实现)](./zookeeper-lock-spring-boot-starter/README.md)

### 使用方式

##### 依赖

- 使用redisson-lock-spring-boot-starter

    引入依赖
    ```
    dependencies {
        compile("com.jean:redisson-lock-spring-boot-starter:1.0-SNAPSHOT")
    }
    ```
    配置文件
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
   > min-idle必须大于0

- 使用使用redis-lock-spring-boot-starter
    引入依赖
    ```
    dependencies {
        compile("com.jean:redis-lock-spring-boot-starter:1.0-SNAPSHOT")
    }
    ```
    配置文件
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
    ```    
- 使用zookeeper-lock-spring-boot-starter
    引入依赖
    ```
    dependencies {
        compile("com.jean:zookeeper-lock-spring-boot-starter:1.0-SNAPSHOT")
    }
    ```
    配置文件
    ```
    //TODO
    ```
##### 使用方式

- 使用`@DistributedLock`注解
    ```
    @Service
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
    注解`@DistributedLock`说明
    
    - name 锁的名称
    - namePrefix 锁的名称前缀，支持SpEL表达式。默认值`"lock"`
    - nameSuffix 锁的名称后缀，可以设置与业务相关的值，支持SpEL表达式。默认值`""`
    - separator 拼接namePrefix、name、nameSuffix的分隔符。默认值`"."`
    - waitTime 尝试加锁最长等待时间。waitTime等于0时，加锁失败不等待。当waitTime小于0时，会一直等待锁。默认值`3`
    - timeout 锁超时时间。锁超时后自动释放。建议尽量缩简需要加锁的逻辑。timeout等于0时不加锁。默认值`30`
    - timeUnit 时间单位。默认值`秒`
    - fallback 加锁失败回调方法，该回调方法的返回值类型和参数必须与当前被加锁的方法一致。默认值`""`

- 编码方式
    ```
    @Service
    public class OrderServiceImpl {
        
        @Autowired
        private LockTemplate lockTemplate
    
        @Override
        @Transactional
        public Order buyProduct(Object param) {
            def worker = new LockWorker<Order>() {
                @Override
                Order work() throws Throwable {
                    //冻结用户账户余额
                    //扣减商品库存
                    //TODO
                    return new Order()
                }
            }
            return lockTemplate.tryLock("lock.buy_product.${param.productId}_${param.userId}", 3L, 30L, TimeUnit.SECONDS, worker)
        }
    }
    ```


> 使用注解方式，锁在事务的外层执行，与注解的顺序无关，即：先加锁，再进入事务。
>
> 使用编码方式，锁在事务内部执行，尽量减少获取所等待时间和锁定时间，避免事务时间太长导致回滚。
>
> redisson锁暂时不支持集群模式。