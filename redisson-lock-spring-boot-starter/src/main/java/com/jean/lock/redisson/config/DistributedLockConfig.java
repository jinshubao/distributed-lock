package com.jean.lock.redisson.config;

import com.jean.lock.redisson.template.RedissonLockTemplate;
import com.jean.lock.template.LockTemplate;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisOperations;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * 配置类
 *
 * @author jinshubao
 * @create 2018/05/14
 */
@ConditionalOnClass({Redisson.class, RedisOperations.class})
@AutoConfigureAfter(RedisAutoConfiguration.class)
@EnableConfigurationProperties(RedisProperties.class)
public class DistributedLockConfig {

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean(RedissonClient.class)
    RedissonClient redissonClient(RedisProperties redisProperties) {
        Config config = new Config();
        Duration duration = redisProperties.getTimeout();
        int timeout = 0;
        if (duration != null) {
            timeout = (int) duration.toMillis();
        }

        if (redisProperties.getSentinel() != null) {
            List<String> list = redisProperties.getSentinel().getNodes();
            String[] nodes = convert(list);
            config.useSentinelServers()
                    .setMasterName(redisProperties.getSentinel().getMaster())
                    .addSentinelAddress(nodes)
                    .setDatabase(redisProperties.getDatabase())
                    .setConnectTimeout(timeout)
                    .setPassword(redisProperties.getPassword());
        } else if (redisProperties.getCluster() != null) {
            List<String> list = redisProperties.getCluster().getNodes();
            String[] nodes = convert(list);
            config.useClusterServers()
                    .addNodeAddress(nodes)
                    .setConnectTimeout(timeout)
                    .setPassword(redisProperties.getPassword());
        } else {
            String prefix = "redis://";
            if (redisProperties.isSsl()) {
                prefix = "rediss://";
            }
            config.useSingleServer()
                    .setAddress(prefix + redisProperties.getHost() + ":" + redisProperties.getPort())
                    .setConnectTimeout(timeout)
                    .setDatabase(redisProperties.getDatabase())
                    .setPassword(redisProperties.getPassword());
        }
        return Redisson.create(config);
    }

    @Bean
    @ConditionalOnMissingBean(LockTemplate.class)
    LockTemplate<RLock> distributedLockTemplate(RedissonClient redissonClient) {
        return new RedissonLockTemplate(redissonClient);
    }


    private String[] convert(List<String> nodesObject) {
        List<String> nodes = new ArrayList<>(nodesObject.size());
        for (String node : nodesObject) {
            if (!node.startsWith("redis://") && !node.startsWith("rediss://")) {
                nodes.add("redis://" + node);
            } else {
                nodes.add(node);
            }
        }
        return nodes.toArray(new String[0]);
    }
}
