package com.sqlpro.l7cache;

import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class L7CachePubService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic channelTopic;

    public void sendMessage(L7CacheMessage message) {
        redisTemplate.convertAndSend(channelTopic.getTopic(), message);
    }
}
