package com.springles.service.impl;

import com.springles.domain.constants.ChatRoomCode;
import com.springles.domain.dto.Chat.ChatRoomListResponseDto;
import com.springles.domain.dto.member.MemberCreateRequest;
import com.springles.domain.entity.ChatRoom;
import com.springles.repository.ChatRoomJpaRepository;
import com.springles.repository.MemberRepository;
import com.springles.service.ChatRoomService;
import com.springles.service.MemberService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
class ChatRoomServiceImplTest {

    @Autowired
    ChatRoomJpaRepository chatRoomRepository;
    @Autowired
    ChatRoomService chatRoomService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MemberService memberService;

    @BeforeEach
    public void init() {

        for (int i = 1; i <= 2; i++) {
            chatRoomRepository.save(
                    new ChatRoom(Long.valueOf(i), "gameRoom" + i, null, Long.valueOf(i), ChatRoomCode.WAITING, 6L, Long.valueOf(i), true) // 오픈 , 대기중
            );
        }

    }


    @Test
    @DisplayName("전체 조회 테스트 ( 오픈된 방 이면서 대기 중인 방만 조회 , 빠른 시작 순으로 )")
    public void findByOpenTrueTest() {

        // given
        chatRoomRepository.save(new ChatRoom(3L, "gameRoom3", "1111", 3L, ChatRoomCode.WAITING, 8L, 7L, false));// 비밀방, 대기중
        ChatRoom gameRoom4 = chatRoomRepository.save(new ChatRoom(4L, "gameRoom4", null, 4L, ChatRoomCode.WAITING, 8L, 5L, true));// 오픈방, 대기중, 시작 3명남음.

        // when
        Page<ChatRoomListResponseDto> allChatRooms = chatRoomService.findAllChatRooms(0, 10);
        List<ChatRoomListResponseDto> content = allChatRooms.getContent();

        // then
        assertThat(content.size()).isEqualTo(3); // 오픈된 방 3개
        assertThat(content.stream().findFirst().get().getTitle()).isEqualTo(gameRoom4.getTitle()); // 빠른시작 가능한 방이 가장 위에 정렬 되는지 테스트

    }

    @Test
    @DisplayName("채팅방 이름 조회")
    public void findByTitleTest() {

        // given
        String title = "gameRoom";

        // when
        List<ChatRoomListResponseDto> findAllByTitle = chatRoomService.findChatRoomByTitle(title);

        // then
        assertThat(findAllByTitle.size()).isEqualTo(2);

    }

    @Test
    @DisplayName("방장 이름으로 조회")
    public void findByOwnerNickNameTest() {

        // given : member init
        memberService.signUp(MemberCreateRequest.builder()
                .memberName("testUser1")
                .password("1")
                .passwordConfirm("1")
                .email("1@")
                .role("user")
                .isDeleted(false).build());
        // ----- member init 끝

        // when
        List<ChatRoomListResponseDto> chatRoomList = chatRoomService.findChatRoomByNickname("testUser1");

        //then
        ChatRoomListResponseDto result = chatRoomList.stream().findAny().get();
        assertThat(result.getTitle()).isEqualTo("gameRoom1");

    }

}