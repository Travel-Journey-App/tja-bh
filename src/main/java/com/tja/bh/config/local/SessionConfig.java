package com.tja.bh.config.local;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;

@Profile(value = "default")
@Configuration
@EnableRedisHttpSession
public class SessionConfig extends AbstractHttpSessionApplicationInitializer {

    @Bean
    public RedisConnectionFactory connectionFactory(@Value("${spring.redis.host}") String redisHost,
                                                    @Value("${spring.redis.port}") int redisPort) {
        return new LettuceConnectionFactory(redisHost, redisPort);
    }
}