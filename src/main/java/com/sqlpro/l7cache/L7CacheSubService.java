package com.sqlpro.l7cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class L7CacheSubService implements MessageListener {
    public static List<String> messageList = new ArrayList<>();
    private final ObjectMapper mapper = new ObjectMapper();

    private final L7Cache l7Cache;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            L7CacheMessage redisMessage = mapper.readValue(message.getBody(), L7CacheMessage.class);
            messageList.add(message.toString());

            log.debug("Received Message: {}", message);
            log.debug("Sender: {}", redisMessage.getSender());
            log.debug("Context: {}", redisMessage.getContext());

            l7Cache.evict(redisMessage.getContext());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
