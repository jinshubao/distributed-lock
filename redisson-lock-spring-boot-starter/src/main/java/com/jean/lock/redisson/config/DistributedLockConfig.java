package com.jean.lock.redisson.config;

import com.jean.lock.manager.LockManager;
import com.jean.lock.redisson.manager.RedissonLockManager;
import com.jean.lock.redisson.template.RedissonLockTemplate;
import com.jean.lock.template.LockTemplate;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 配置类
 *
 * @author jinshubao
 * @create 2018/05/14
 */
@EnableConfigurationProperties(RedisProperties.class)
public class DistributedLockConfig {

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    RedissonClient redissonClient(RedisProperties properties) {
        Config config = new Config();
        SingleServerConfig serverConfig = config.useSingleServer()
                .setAddress("redis://" + properties.getHost() + ":" + properties.getPort())
                .setPassword(properties.getPassword())
                .setDatabase(properties.getDatabase());
        RedisProperties.Pool pool = properties.getPool();
        if (pool != null) {
            serverConfig.setConnectionPoolSize(pool.getMaxActive());
            serverConfig.setConnectionMinimumIdleSize(pool.getMinIdle());
        }
        return Redisson.create(config);
    }

    @Bean
    @ConditionalOnMissingBean
    LockManager<RLock> lockManager(RedissonClient redissonClient) {
        return new RedissonLockManager(redissonClient);
    }

    @Bean
    @ConditionalOnMissingBean
    LockTemplate<RLock> distributedLockTemplate(LockManager<RLock> lockManager) {
        return new RedissonLockTemplate(lockManager);
    }
}
