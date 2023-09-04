package com.springles.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springles.repository.PlayerRedisRepository;
import com.springles.service.GameSessionVoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class NightVoteManager {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final GameSessionManager gameSessionManager;
    private final PlayerRedisRepository playerRedisRepository;
    private final GameSessionVoteService gameSessionVoteService;
    private final ObjectMapper objectMapper;

    public void sendMessage(String message) {

    }
}
