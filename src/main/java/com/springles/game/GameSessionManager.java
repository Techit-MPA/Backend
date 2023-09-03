package com.springles.game;

import com.springles.domain.constants.GamePhase;
import com.springles.domain.constants.GameRole;
import com.springles.domain.dto.chatroom.ChatRoomReqDTO;
import com.springles.domain.dto.chatroom.ChatRoomUpdateReqDto;
import com.springles.domain.entity.ChatRoom;
import com.springles.domain.entity.GameSession;
import com.springles.domain.entity.Member;
import com.springles.domain.entity.Player;
import com.springles.exception.CustomException;
import com.springles.exception.constants.ErrorCode;
import com.springles.repository.GameSessionRedisRepository;
import com.springles.repository.MemberJpaRepository;
import com.springles.repository.PlayerRedisRepository;
import groovy.util.logging.Slf4j;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class GameSessionManager {

    private final GameSessionRedisRepository gameSessionRedisRepository;
    private final PlayerRedisRepository playerRedisRepository;
    private final RoleManager roleManager;
    private final MemberJpaRepository memberJpaRepository;

    /* 게임 세션 생성 */
    public void createGame(ChatRoom chatRoom) {
        gameSessionRedisRepository.save(GameSession.of(chatRoom));
        Member member = memberJpaRepository.findById(chatRoom.getOwnerId())
            .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        addUser(chatRoom.getId(), member.getMemberName());
    }

    /* 게임 세션 수정 */

    /* 이름으로 채팅방 찾기(페이징) */


    /* 방장 닉네임으로 채팅방 찾기 (페이징) */

    /* 방Id로 채팅방 찾기 */

    /* 닉네임과 제목 통합으로 채팅방 찾기 (페이징) */

    /* 모든 채팅방 조회 */

    /* 채팅방 조건 조회 */

    /* 빠른 입장 */

    /* 투표 세션마다 투표 대상 플레이어 리스트 조회 */
    /*
     * 전체 투표 : 전체
     * 경찰 투표 : 전체
     * 마피아 투표 : 마피아를 제외한 전체
     * 의사 투표 : 전체
     * */

    /* 게임 시작 */
    public List<Player> startGame(Long roomId, String memberName) {
        GameSession gameSession = findGameByRoomId(roomId);
        Player player = findPlayerByMemberName(memberName);
        if (!Objects.equals(gameSession.getHostId(), player.getMemberId())) {
            throw new CustomException(ErrorCode.NOT_AUTHORIZED_CONTENT);
        }
        List<Player> players = findPlayersByRoomId(roomId);
        if (players.size() < 5 || players.size() > 10) {
            throw new CustomException(ErrorCode.PLAYER_HEAD_ERROR);
        }
        roleManager.assignRole(players);
        gameSessionRedisRepository.save(gameSession.start(players.size()));
        return players;
    }

    /* 게임 종료 -> 준비 상태로 돌아가기 */
    public void endGame(Long roomId) {
        GameSession gameSession = findGameByRoomId(roomId);
        gameSession.end();
        gameSessionRedisRepository.save(gameSession);
        List<Player> players = findPlayersByRoomId(roomId);
        for (Player player : players) {
            player.updateRole(GameRole.NONE);
        }
        playerRedisRepository.saveAll(players);

    }

    /* 게임 세션 삭제 */
    public void removeGame(Long roomId) {
        List<Player> players = findPlayersByRoomId(roomId);
        if (!players.isEmpty()) {
            throw new CustomException(ErrorCode.GAME_PLAYER_EXISTS);
        }
        gameSessionRedisRepository.deleteById(roomId);
    }

    /* 게임에서 유저 제거 */
    public void removePlayer(Long roomId, String memberName) {
        Player player = findPlayerByMemberName(memberName);
        GameSession gameSession = findGameByRoomId(roomId);
        Member member = findMemberByMemberName(memberName);
        playerRedisRepository.deleteById(member.getId());
        List<Player> players = findPlayersByRoomId(roomId);
        // 아무도 없다면 방삭제
        if (players.isEmpty()) {
            removeGame(roomId);
        }
        // 남은 플레이어가 존재하고 방장이 나갔다면 랜덤으로 방장 넘겨주기
        else if (Objects.equals(gameSession.getHostId(), member.getId())) {
            Random random = new Random();
            gameSession.changeHost(players.get(random.nextInt(players.size())).getMemberId());
            gameSessionRedisRepository.save(gameSession);
        }
    }

    /* 게임에 유저 추가 */
    public void addUser(Long roomId, String memberName) {
        if (playerRedisRepository.findByRoomId(roomId).size() > 10) {
            throw new CustomException(ErrorCode.GAME_HEAD_FULL);
        }
        GameSession gameSession = findGameByRoomId(roomId);
        Member member = findMemberByMemberName(memberName);
        if (playerRedisRepository.existsByMemberName(memberName)) {
            throw new CustomException(ErrorCode.PLAYER_STILL_INGAME);
        }
        playerRedisRepository.save(Player.of(member.getId(), roomId, memberName));
    }

    /* 게임 정보 업데이트 */
    public void updateGame(Long roomId, String memberName, ChatRoomUpdateReqDto chatRoomUpdateReqDto) {
        GameSession gameSession = findGameByRoomId(roomId);
        if (!gameSession.getGamePhase().equals(GamePhase.READY)) {
            // 게임 상태 익셉션 -> 게임 중 정보 변경 불가
        }
        Player player = findPlayerByMemberName(memberName);
        if (!gameSession.getHostId().equals(player.getMemberId())) {
            // 권한 익셉션 -> 방장만 정보를 변경할 수 있음
        }

    }

    public GameSession findGameByRoomId(Long roomId) {
        return gameSessionRedisRepository.findById(roomId)
            .orElseThrow(() -> new CustomException(ErrorCode.GAME_NOT_FOUND));
    }

    public GameSession findGamByHostId(Long hostId) {
        return gameSessionRedisRepository.findByHostId(hostId)
            .orElseThrow(() -> new CustomException(ErrorCode.GAME_NOT_FOUND));
    }

    public List<Player> findPlayersByRoomId(Long roomId) {
        return playerRedisRepository.findByRoomId(roomId);
    }

    public Player findPlayerByMemberName(String memberName) {
        return playerRedisRepository.findByMemberName(memberName)
            .orElseThrow(() -> new CustomException(ErrorCode.PLAYER_NOT_FOUND));
    }

    public Member findMemberByMemberName(String memberName) {
        return memberJpaRepository.findByMemberName(memberName)
            .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }

    public boolean existRoomByRoomId(Long roomId) {
        return gameSessionRedisRepository.existsById(roomId);
    }
}
