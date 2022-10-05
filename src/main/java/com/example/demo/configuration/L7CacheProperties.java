package com.example.demo.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Getter
@Configuration
@ConfigurationProperties(prefix = "spring.l7cache")
@ConditionalOnMissingBean(L7CacheProperties.class )
public class L7CacheProperties {

	private String redisHost;

	private int redisPort;

	private long ttl;
}
