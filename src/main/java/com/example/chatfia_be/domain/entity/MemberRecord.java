package com.example.chatfia_be.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberRecord {

    // 게임 정보 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 시민 횟수
    @Column(nullable = false)
    private Long citizenCnt;

    @Column(nullable = false)
    private Long doctorCnt;

    @Column(nullable = false)
    private Long policeCnt;

    @Column(nullable = false)
    private Long saveCnt;

    @Column(nullable = false)
    private Long killCnt;

    @Column(nullable = false)
    private Long mafiaWinCnt;

    @Column(nullable = false)
    private Long citizenWinCnt;

    @Column(nullable = false)
    private Long totalCnt;

    @Column(nullable = false)
    private Long totalTime;

}