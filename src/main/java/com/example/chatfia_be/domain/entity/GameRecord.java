package com.example.chatfia_be.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@Entity
@Table(name ="game_record")
@NoArgsConstructor
@AllArgsConstructor
public class GameRecord {

    // 게임 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 게임방 제목
    @Column(nullable = false)
    private String title;

    // 방장 ID
    @Column(nullable = false)
    private Long ownerId;

    // 게임방 상태(게임 진행중, 게임 참여 가능)
    // enum으로 수정할 것
    @Column(nullable = false)
    private String state;

    // 정원
    @Column(nullable = false)
    private Long capacity;

    // 참여자 수
    @Column(nullable = false)
    private Long head;

    // 공개방 / 비밀방
    @Column(nullable = false)
    private boolean open;

    // 이긴팀
    @Column(nullable = false)
    private boolean winner;

    // 게임 진행 시간
    @Column( nullable = false)
    private LocalDateTime duration;
}
