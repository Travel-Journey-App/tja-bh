package com.tja.bh.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@TestConfiguration
public class LocalTestConfiguration {
    private final RedisServer redisServer;

    public LocalTestConfiguration(@Value("${spring.redis.port}") int redisPort) {
        this.redisServer = RedisServer.builder()
                .port(redisPort)
                .setting("maxmemory 128M")
                .build();

    }

    @PostConstruct
    public void postConstruct() {
        redisServer.start();
    }

    @PreDestroy
    public void preDestroy() {
        redisServer.stop();
    }
}
