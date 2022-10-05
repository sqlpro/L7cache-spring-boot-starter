// package com.example.demo.controller;
//
// import java.util.concurrent.TimeUnit;
//
// import org.cache2k.Cache;
// import org.cache2k.Cache2kBuilder;
// import org.springframework.context.annotation.Lazy;
// import org.springframework.stereotype.Component;
//
// import com.example.demo.service.RedisPubService;
//
// import lombok.RequiredArgsConstructor;
//
// @Lazy
// @Component
// @RequiredArgsConstructor
// public final class L7CacheFactoryBean {
//
// 	private final RedisPubService redisPubService;
//
// 	public <T> L7Cache<T> bake() {
// 		return new L7Cache<>(redisPubService, defaultCoreCache());
// 	}
//
// 	public <T> L7Cache<T> bake(Cache<String, Object> cache) {
// 		return new L7Cache<>(redisPubService, cache);
// 	}
//
// 	// TODO : Auto Configuration으로 교체
// 	private Cache<String, Object> defaultCoreCache() {
// 		return Cache2kBuilder.of(String.class, Object.class)
// 			.name("L7-CACHE")
// 			.eternal(true)
// 			.entryCapacity(100)
// 			.expireAfterWrite(6_000, TimeUnit.MILLISECONDS)
// 			.build();
// 	}
// }
