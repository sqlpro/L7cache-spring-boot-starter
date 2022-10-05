package com.example.demo.controller;

import java.util.Calendar;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.cache2k.Cache;

import com.example.demo.service.CacheMessage;
import com.example.demo.service.RedisPubService;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class L7Cache<T> {

	@Getter
	private final String cacheInstanceId = UUID.randomUUID().toString();

	private final Cache<String, Object> cache;
	private final AtomicInteger cacheMissCount;
	private final Object lockObject;

	private final RedisPubService redisPubService;

	public L7Cache(RedisPubService redisPubService, Cache<String, Object> cache) {
		this.cache = cache;
		this.redisPubService = redisPubService;

		this.lockObject = new Object();
		this.cacheMissCount = new AtomicInteger(0);
	}

	public T get(String key, L7CacheFunctionInterface<T> callback) {
		T cachedObject = (T)this.cache.get(key);
		synchronized (lockObject) {
			if (Objects.isNull(cachedObject)) {
				synchronized (lockObject) {
					cacheMissCount.incrementAndGet();
					cachedObject = callback.apply();
					this.cache.put(key, cachedObject);
				}
			}
		}
		return cachedObject;
	}

	/**
	 * 캐시의 유효 기간을 연장합니다
	 * @param key
	 */
	public void touch(String key) {
		this.touch(key, 600_000);
	}
	public void touch(String key, long ms) {
		this.cache.expireAt(key, Calendar.getInstance().getTimeInMillis() + ms);
	}

	public void evict(String key) {
		log.info("{} 키에 대한 캐싱을 Evict 합니다", key);
		this.cache.remove(key);
	}

	public void renew(String key) {
		// 삭제와 이벤트의 무한 루프를 방지하기 위해 직접 캐시를 삭제하지 않고, Redis에 삭제 메시지만 발행한다. 이 메시지가 도착하면 자동으로 캐시가 갱신된다.
		this.redisPubService.sendMessage(new CacheMessage(cacheInstanceId, key));
	}
}
