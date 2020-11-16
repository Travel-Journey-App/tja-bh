package com.tja.bh.config;

import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;

import java.net.URI;
import java.net.URISyntaxException;

@Profile(value = "prod")
@Configuration
@EnableRedisHttpSession
public class SessionConfig extends AbstractHttpSessionApplicationInitializer {
    @Bean
    ConfigureRedisAction configureRedisAction() {
        return ConfigureRedisAction.NO_OP;
    }

    @Bean
    public RedisConnectionFactory jedisConnectionFactory() throws URISyntaxException {
        val redisURI = new URI(System.getenv("REDIS_URL"));
        val redisHost = redisURI.getHost();
        val redisPort = redisURI.getPort();
        val redisUserInfo = redisURI.getUserInfo().split(":");
        val redisPassword = redisUserInfo.length == 2 ? redisUserInfo[1] : "";

        val standaloneConfig = new RedisStandaloneConfiguration(redisHost, redisPort);
        standaloneConfig.setPassword(RedisPassword.of(redisPassword));

        return new JedisConnectionFactory(standaloneConfig);
    }

    @Bean
    public RedisTemplate<Object, Object> redisTemplate() throws URISyntaxException {
        val redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        return redisTemplate;
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig())
                .transactionAware()
                .build();
    }
}