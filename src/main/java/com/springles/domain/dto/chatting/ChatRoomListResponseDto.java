package com.springles.domain.dto.chatting;

import com.springles.domain.constants.ChatRoomCode;
import com.springles.domain.entity.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomListResponseDto {

    private Long id; // 채팅방 ID
    private String title; // 채팅방 이름
    private Long capacity; // 정원
    private Long head; // 참여자 수
    private ChatRoomCode state; // 채팅방 상태
    private boolean close; // 공개방 , 비밀방

    public static ChatRoomListResponseDto fromEntity(ChatRoom chatRoom) {
        return ChatRoomListResponseDto.builder()
                .id(chatRoom.getId())
                .title(chatRoom.getTitle())
                .capacity(chatRoom.getCapacity())
                .head(chatRoom.getHead())
                .state(chatRoom.getState())
                .close(chatRoom.getClose())
                .build();
    }
}
