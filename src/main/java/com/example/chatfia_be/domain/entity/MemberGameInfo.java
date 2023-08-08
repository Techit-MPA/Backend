package com.example.chatfia_be.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "member_game_info")
public class MemberGameInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    // 게임정보 아이디

    @Column(nullable = false)
    private String nickname;    // 게임 닉네임

    @Column(nullable = false)
    private Long level; // 유저 레벨

    @Column(nullable = false)
    private Long exp;   // 유저 경험치

    // enum으로 변경 필요
    private String inGameRole;  // 게임 내 직업
}
