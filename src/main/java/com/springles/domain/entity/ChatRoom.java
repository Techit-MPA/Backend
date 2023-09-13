package com.springles.domain.entity;


import com.springles.domain.constants.ChatRoomCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChatRoom {

    // 채팅방 id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatroom_id")
    private Long id;

    // 채팅방 제목
    @Column(nullable = false)
    private String title;

    // 채팅방 PW
    private String password;

    // 방장
    @Column(nullable = false)
    private Long ownerId;

    // 채팅방 상태 ( WAITING / PLAYING )
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ChatRoomCode state;

    // 채팅방 정원
    @Column(nullable = false)
    private Long capacity;

    // 참여자 수
    @Column(nullable = false)
    private Long head;

    // 공개방/비밀방
    @Column(nullable = false)
    private Boolean close;

    // 채팅방 수정
    public void modify(ChatRoom chatRoom){
        this.title = chatRoom.getTitle();
        this.password = chatRoom.getPassword();
        this.ownerId = chatRoom.getOwnerId();
        this.state = chatRoom.getState();
        this.capacity = chatRoom.getCapacity();
        this.head = chatRoom.getHead();
        this.close = chatRoom.getClose();
    }

    // 채팅방 참여 시 인원 + 1
    public void headPlus(ChatRoom chatRoom){
        chatRoom.head += 1;
    }

    // 채팅방 참여 시 인원 - 1
    public void headMinus(ChatRoom chatRoom){
        chatRoom.head -= 1;
    }


    // 채팅방 방장 수정
    public void changeHost(Long nextHostId) {
        this.ownerId = nextHostId;
    }
}