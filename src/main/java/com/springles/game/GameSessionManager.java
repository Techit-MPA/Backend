package com.springles.game;

import com.springles.domain.constants.GamePhase;
import com.springles.domain.constants.GameRole;
import com.springles.domain.entity.ChatRoom;
import com.springles.domain.entity.GameRecord;
import com.springles.domain.entity.GameSession;
import com.springles.domain.entity.Member;
import com.springles.domain.entity.Player;
import com.springles.exception.CustomException;
import com.springles.exception.constants.ErrorCode;
import com.springles.repository.ChatRoomJpaRepository;
import com.springles.repository.GameRecordJpaRepository;
import com.springles.repository.GameSessionRedisRepository;
import com.springles.repository.MemberJpaRepository;
import com.springles.repository.PlayerRedisRepository;
import com.springles.service.MemberService;
import com.springles.service.impl.MemberServiceImpl;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final ChatRoomJpaRepository chatRoomJpaRepository;
    private final MessageManager messageManager;
    private final GameRecordJpaRepository gameRecordJpaRepository;
    private final MemberService memberService;

    /* 게임 세션 생성 */
    public void createGame(Long roomId) {
        ChatRoom chatRoom = chatRoomJpaRepository.findByIdCustom(roomId);
        gameSessionRedisRepository.save(GameSession.of(chatRoom));
    }

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
        ChatRoom chatRoom = chatRoomJpaRepository.findByIdCustom(roomId);
        boolean winner = gameSession.getAliveMafia() == 0;
        
        // GameRecord 저장
        GameRecord gameRecord = GameRecord.builder()
            .title(chatRoom.getTitle())
            .ownerId(gameSession.getHostId())
            .capacity(chatRoom.getCapacity())
            .head(chatRoom.getHead())
            .open(chatRoom.getClose())
            .winner(winner)
            .duration(
                (int) ChronoUnit.MINUTES.between(LocalDateTime.now(), gameSession.getStartTime()))
            .build();

        List<Member> memberList = new ArrayList<>();
        for (Player player : findPlayersByRoomId(roomId)) {
            memberList.add(findMemberByMemberName(player.getMemberName()));
        }
        for (Member member : memberList) {
            gameRecord.addMember(member);
            member.addGameRecord(gameRecord);
            memberService.levelUp(member.getId(),gameRecord);
        }

        gameRecordJpaRepository.save(gameRecord);

        // MemberGameInfo 반영

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
        chatRoomJpaRepository.deleteById(roomId);
        gameSessionRedisRepository.deleteById(roomId);
    }

    /* 게임에서 유저 제거 */
    public void removePlayer(Long roomId, String memberName) {
        Player player = findPlayerByMemberName(memberName);
        GameSession gameSession = findGameByRoomId(roomId);
        playerRedisRepository.deleteById(player.getMemberId());
        List<Player> players = findPlayersByRoomId(roomId);
        // 아무도 없다면 방삭제
        if (players.isEmpty()) {
            removeGame(roomId);
        }
        // 남은 플레이어가 존재하고 방장이 나갔다면 랜덤으로 방장 넘겨주기
        else if (Objects.equals(gameSession.getHostId(), player.getMemberId())) {
            Random random = new Random();
            Player nextHost = players.get(random.nextInt(players.size()));
            gameSession.changeHost(nextHost.getMemberId());
            chatRoomJpaRepository.findByIdCustom(roomId).changeHost(nextHost.getMemberId());
            messageManager.sendMessage(
                "/sub/chat/"+roomId,
                nextHost.getNickName()+"님이 방장이 되었습니다.",
                roomId, "admin");
            gameSessionRedisRepository.save(gameSession);
        }
    }

    /* 게임에 유저 추가 */
    public List<Player> addUser(Long roomId, String memberName) {
        if (playerRedisRepository.findByRoomId(roomId).size() > 10) {
            throw new CustomException(ErrorCode.GAME_HEAD_FULL);
        }
        GameSession gameSession = findGameByRoomId(roomId);
        Member member = findMemberByMemberName(memberName);
        if (playerRedisRepository.existsByMemberName(memberName)) {
            throw new CustomException(ErrorCode.PLAYER_STILL_INGAME);
        }
        playerRedisRepository.save(Player.of(member.getId(), roomId, memberName, member.getMemberGameInfo().getNickname()));
        return findPlayersByRoomId(roomId);
    }

    public GameSession findGameByRoomId(Long roomId) {
        return gameSessionRedisRepository.findByRoomId(roomId)
            .orElseThrow(() -> new CustomException(ErrorCode.GAME_NOT_FOUND));
    }

    public GameSession findGameByHostId(Long hostId) {
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

    public void changePhase(Long roomId, GamePhase phase) {
        GameSession gameSession = findGameByRoomId(roomId);
        gameSession.setGamePhase(phase);
        gameSessionRedisRepository.save(gameSession);
    }

    public void saveSession(GameSession gameSession) {

        gameSessionRedisRepository.save(gameSession);
    }

    public boolean isEnd(GameSession gameSession) {
        if (gameSession.getAliveMafia() == 0) {
            return true;
        }
        else if (gameSession.getDay() > 20) {
            return true;
        }
        else if (gameSession.getAliveMafia() >= gameSession.getAlivePolice() + gameSession.getAliveDoctor() + gameSession.getAliveCivilian()) {
            return true;
        }
        return false;
    }

    public int mafiaWin(GameSession gameSession) {
        if (gameSession.getAliveMafia() >= gameSession.getAlivePolice() + gameSession.getAliveDoctor() + gameSession.getAliveCivilian()) {
            return 1;
        }
        else if (gameSession.getAliveMafia() == 0) {
            return 0;
        }
        else return 2;
    }
}
