package com.example.demo.service;

import com.example.demo.controller.L7Cache;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cache2k.Cache;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisSubService implements MessageListener {
    public static List<String> messageList = new ArrayList<>();
    private final ObjectMapper mapper = new ObjectMapper();

    private final L7Cache l7Cache;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            CacheMessage redisMessage = mapper.readValue(message.getBody(), CacheMessage.class);
            messageList.add(message.toString());

            log.debug("Received Message: {}", message.toString());
            log.debug("Sender: {}", redisMessage.getSender());
            log.debug("Context: {}", redisMessage.getContext());

            l7Cache.evict(redisMessage.getContext(), redisMessage.getSender());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
