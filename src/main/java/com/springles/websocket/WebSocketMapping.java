
package com.springles.websocket;

import com.springles.game.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketMapping {
    // STOMP over WebSocket 요청을 보낼 때 사용하는 용도
    // session에 메시지를 썼던 것처럼
    private final SimpMessagingTemplate simpMessagingTemplate;

    // RequestMapping 에 대응
    @MessageMapping("/chat")
    public void sendChat(
            ChatMessage chatMessage,
            @Headers Map<String, Object> headers,
            @Header("nativeHeaders") Map<String, Object> nativeHeaders
    ) {
        log.info(chatMessage.toString());
        log.info(headers.toString());
        log.info(nativeHeaders.toString());
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        chatMessage.setTime(time);
        simpMessagingTemplate.convertAndSend(
                String.format("/topic/%s", chatMessage.getRoomId()),
                chatMessage
        );
    }
}