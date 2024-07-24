package com.commerce.couponcore.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.sentinel.master}")
    private String master;

    @Value("${spring.data.redis.sentinel.nodes}")
    private String sentinelNodes;

    @Value("${spring.data.redis.password}")
    private String password;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        String[] nodes = sentinelNodes.split(",");
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = "redis://" + nodes[i].trim();
        }
        config.useSentinelServers()
                .setMasterName(master)
                .addSentinelAddress(nodes)
                .setPassword(password);
        return Redisson.create(config);
    }

    @Bean
    public RedissonConnectionFactory redissonConnectionFactory() {
        return new RedissonConnectionFactory(redissonClient());
    }

    @Bean
    public StringRedisTemplate redisTemplate() {
        var redisTemplate = new StringRedisTemplate();
        redisTemplate.setConnectionFactory(redissonConnectionFactory());
        return redisTemplate;
    }
}