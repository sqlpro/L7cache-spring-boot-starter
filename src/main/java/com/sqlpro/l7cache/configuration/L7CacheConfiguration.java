package com.sqlpro.l7cache.configuration;

import com.sqlpro.l7cache.L7Cache;
import com.sqlpro.l7cache.L7CacheProperties;
import com.sqlpro.l7cache.L7CachePubService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.cache2k.config.Cache2kConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableRedisRepositories
public class L7CacheConfiguration {

    private final L7CacheProperties l7CacheProperties;
    private final L7CachePubService redisPubService;

    @Bean
    @ConditionalOnMissingBean(L7Cache.class)
    Cache<String, Object> cache() {
        return Cache2kBuilder.of(String.class, Object.class)
                .name(l7CacheProperties.getName())
                .eternal(l7CacheProperties.isEternal())
                .entryCapacity(l7CacheProperties.getEntryCapacity() == -1 ? Cache2kConfig.DEFAULT_ENTRY_CAPACITY : l7CacheProperties.getEntryCapacity())
                .expireAfterWrite(l7CacheProperties.getTtl(), TimeUnit.MILLISECONDS)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(L7Cache.class)
    <T> L7Cache<T> l7Cache() {
        return new L7Cache<>(redisPubService, cache());
    }
}
