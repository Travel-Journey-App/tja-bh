package com.tja.bh.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = "com.tja.bh.persistence.model")
@EnableJpaRepositories(basePackages = "com.tja.bh.persistence.repository")
public class PersistenceJPAConfig {
}
