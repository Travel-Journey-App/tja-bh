package com.tja.bh.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.logging.Logger;

@TestConfiguration
public class LocalTestConfiguration {
    private static final Logger log = Logger.getLogger("LocalTestConfiguration");

    private final RedisServer redisServer;

    public LocalTestConfiguration(@Value("${spring.redis.port}") int redisPort) {
        this.redisServer = RedisServer.builder()
                .port(redisPort)
                .setting("maxmemory 64M")
                .build();

    }

    @PostConstruct
    public void postConstruct() {
        if (!redisServer.isActive()) {
            try {
                redisServer.start();
            } catch (Exception e) {
                log.warning(e.getMessage());
            }
        }
    }

    @PreDestroy
    public void preDestroy() {
        if (redisServer.isActive()) {
            try {
                redisServer.stop();
            } catch (Exception e) {
                log.warning(e.getMessage());
            }
        }
    }
}
