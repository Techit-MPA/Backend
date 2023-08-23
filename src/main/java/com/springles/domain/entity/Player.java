package com.springles.domain.entity;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@Builder
@RedisHash(value = "Player")
public class Player {

    @Id
    private final String memberId;

    @Indexed
    private final String roomId;

    private final String role;
}
