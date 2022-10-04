package com.example.demo.controller;

import com.example.demo.service.CacheMessage;
import com.example.demo.service.RedisPubService;
import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.cache2k.CacheEntry;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class L7Cache<T> {

    private final String cacheId = UUID.randomUUID().toString();

    private final Cache<String, Object> cache;
    private final AtomicInteger cacheMissCount;
    private final Object lockObject;

    private final RedisPubService redisPubService;

    public L7Cache(RedisPubService redisPubService) {

        this.cacheMissCount = new AtomicInteger(0);
        this.redisPubService = redisPubService;
        this.lockObject = new Object();

//      (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]
        this.cache = Cache2kBuilder.of(String.class, Object.class)
                .name("L7-CACHE")
                .eternal(true)
                .entryCapacity(100)
                .expireAfterWrite(6_000, TimeUnit.MILLISECONDS)
                .build();
    }

    public T get(String key, L7CacheFunctionInterface<T> callback) {
        T cachedObject = (T) this.cache.get(key);
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

    public void touch(String key) {
        this.cache.expireAt(key, Calendar.getInstance().getTimeInMillis() + 600_000);
    }

    public void evict(String key, String sender) {
        this.cache.remove(key);
    }

    public void renew(String key) {
        // 삭제와 이벤트의 무한 루프를 방지하기 위해 직접 캐시를 삭제하지 않고, Redis에 삭제 메시지만 발행한다. 이 메시지가 도착하면 자동으로 캐시가 갱신된다.
        this.redisPubService.sendMessage(new CacheMessage(cacheId, key));
    }
}
