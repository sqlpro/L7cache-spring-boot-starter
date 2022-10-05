package com.example.demo.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.example.demo.service.CacheMessage;
import com.example.demo.service.RedisSubService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableRedisRepositories
public class L7CacheAutoConfiguration {

    private final L7CacheProperties l7CacheProperties;

    @Value("${spring.l7cache.redis.topic:l7cache-bus}")
    private String L7CACHE_REDIS_TOPIC;

    @Bean
    @ConditionalOnMissingBean(RedisTemplate.class)
    public RedisConnectionFactory redisConnectionFactory() {
        String host = l7CacheProperties.getRedisHost();
        int port = l7CacheProperties.getRedisPort();

        return new LettuceConnectionFactory(host, port);
    }

    @Bean
    @ConditionalOnMissingBean(RedisTemplate.class)
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(CacheMessage.class));
        return redisTemplate;
    }

    @Lazy
    @Bean
    MessageListenerAdapter messageListenerAdapter(RedisSubService redisSubService) {
        return new MessageListenerAdapter(redisSubService);
    }

    @Bean
    @DependsOn({"topic"})
    RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory, MessageListenerAdapter messageListenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(messageListenerAdapter, topic());
        return container;
    }

    @Bean
    public ChannelTopic topic() {
        return new ChannelTopic(L7CACHE_REDIS_TOPIC);
    }
}
