package com.springles.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class Subscriber implements MessageListener {
    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;
    private final RedisTemplate redisTemplate;

    private String roomId;

    public void onMessage(Message message, byte[] pattern) {
        try {
        log.info("message 도착:"+message.toString());
        String publishMessage = (String) redisTemplate.getStringSerializer().deserialize(message.getBody());
        ChatMessage chatMessage = objectMapper.readValue(publishMessage, ChatMessage.class);
        messagingTemplate.convertAndSend("/sub/chat/room/"+chatMessage.getRoomId(),chatMessage);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }
}