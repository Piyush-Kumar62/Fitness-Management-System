package com.project.fitness.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Component("redis")
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis", matchIfMissing = true)
public class RedisHealthIndicator implements HealthIndicator {

    private final RedisConnectionFactory connectionFactory;

    @Value("${spring.data.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.data.redis.port:6379}")
    private int redisPort;

    @Override
    public Health health() {
        try {
            connectionFactory.getConnection().ping();
            return Health.up()
                    .withDetail("host", redisHost)
                    .withDetail("port", redisPort)
                    .build();
        } catch (Exception ex) {
            log.warn("Redis health check failed: {}", ex.getMessage());
            return Health.down()
                    .withDetail("error", ex.getMessage())
                    .build();
        }
    }
}
