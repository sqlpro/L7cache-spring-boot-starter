package com.sqlpro.l7cache.configuration;

import com.sqlpro.l7cache.L7CacheProperties;
import com.sqlpro.l7cache.L7CacheMessage;
import com.sqlpro.l7cache.L7CacheSubService;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
public class L7CacheRedisConfiguration {

    private final RedisConnectionFactory redisConnectionFactory;
    private final L7CacheProperties l7CacheProperties;

    @Bean
    @ConditionalOnMissingBean(RedisTemplate.class)
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(L7CacheMessage.class));
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

    @Lazy
    @Bean
    MessageListenerAdapter messageListenerAdapter(L7CacheSubService subscriptionService) {
        return new MessageListenerAdapter(subscriptionService);
    }

    @Lazy
    @Bean
    RedisMessageListenerContainer redisMessageListenerContainer(MessageListenerAdapter messageListenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(messageListenerAdapter, channelTopic());
        return container;
    }

    @Lazy
    @Bean
    public ChannelTopic channelTopic() {
        return new ChannelTopic(l7CacheProperties.getTopic());
    }
}
