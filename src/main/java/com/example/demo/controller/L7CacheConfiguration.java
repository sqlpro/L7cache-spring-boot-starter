package com.example.demo.controller;

import java.util.concurrent.TimeUnit;

import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.service.RedisPubService;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class L7CacheConfiguration {

	private final RedisPubService redisPubService;
	private final L7CacheProperties l7CacheProperties;

	@Bean
	@ConditionalOnMissingBean(L7Cache.class)
	Cache<String, Object> cache() {
		return Cache2kBuilder.of(String.class, Object.class)
			.name("L7-CACHE")
			.eternal(true)
			.entryCapacity(100)
			.expireAfterWrite(6_000, TimeUnit.MILLISECONDS)
			.build();
	}

	@Bean
	@ConditionalOnMissingBean(L7Cache.class)
	<T> L7Cache<T> l7Cache() {
		return new L7Cache<>(redisPubService, cache());
	}
}
