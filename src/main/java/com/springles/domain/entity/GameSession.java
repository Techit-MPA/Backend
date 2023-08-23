package com.springles.domain.entity;

import com.springles.domain.constants.GamePhase;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@Builder
@RedisHash(value = "GameSession")
public class GameSession {

    @Id
    private final String roomId; // 방 id로 아이디 설정

    @Indexed
    private final String hostId; // 방장 아이디로도 검색 가능

    @Enumerated(EnumType.STRING)
    private final GamePhase gamePhase; // 게임 진행 상태

    private int aliveCivilian; // 살아 있는 시민 수

    private int aliveMafia; // 살아 있는 마피아 수

    private int aliveDoctor; // 살아 있는 의사 수

    private int alivePolice; // 살아 있는 경찰 수

}
