package com.springles.controller.message;

import com.springles.domain.constants.GamePhase;
import com.springles.domain.constants.GameRole;
import com.springles.domain.dto.message.DayDiscussionMessage;
import com.springles.domain.dto.message.DayEliminationMessage;
import com.springles.domain.dto.message.NightVoteMessage;
import com.springles.domain.dto.vote.ConfirmResultResponseDto;
import com.springles.domain.dto.vote.GameSessionVoteRequestDto;
import com.springles.domain.dto.vote.VoteResultResponseDto;
import com.springles.domain.entity.GameSession;
import com.springles.domain.entity.Player;
import com.springles.exception.CustomException;
import com.springles.exception.constants.ErrorCode;
import com.springles.game.*;
import com.springles.repository.PlayerRedisRepository;
import com.springles.service.GameSessionVoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
public class VoteController {
    private final GameSessionManager gameSessionManager;
    private final GameSessionVoteService gameSessionVoteService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final PlayerRedisRepository playerRedisRepository;
    private final MessageManager messageManager;
    private final DayDiscussionManager dayDiscussionManager;
    private final DayEliminationManager dayEliminationManager;

    @MessageMapping("/chat/{roomId}/dayStart")
    private void voteStart (SimpMessageHeaderAccessor accessor,
                            @DestinationVariable Long roomId) {
        GameSession gameSession = gameSessionManager.findGameByRoomId(roomId);
        gameSession.changePhase(GamePhase.DAY_VOTE, 100);
        gameSession.passADay();
        gameSessionManager.update(gameSession);

        // 종료된 게임인지 체크
        if (!gameSessionManager.existRoomByRoomId(roomId)) {
            throw new CustomException(ErrorCode.GAME_NOT_FOUND);
        }

        log.info("Room {} start Day {} {} ", gameSession.getRoomId(), gameSession.getDay(),
                gameSession.getGamePhase());

        List<Player> players = playerRedisRepository.findByRoomId(gameSession.getRoomId());

        Map<Long, GameRole> alivePlayerMap = new HashMap<>();
        for (Player player : players) {
            log.info("Room {} has Player {} ", gameSession.getRoomId(), player.getMemberName());
            if (player.isAlive()) {
                alivePlayerMap.put(player.getMemberId(), player.getRole());
            }
        }

        gameSessionVoteService.startVote(roomId, gameSession.getPhaseCount(),
                gameSession.getGamePhase(), gameSession.getTimer(), alivePlayerMap);
        int day = gameSession.getDay();

        log.info("{} 번째 날 아침이 밝았습니다. 투표를 시작합니다.", day);
        // 투표 시작 메시지 전송
        messageManager.sendMessage(
                "/sub/chat/" + roomId,
                 day + "번째 날 아침이 밝았습니다. 투표를 시작합니다.",
                roomId, "admin"
        );

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        Runnable task = () -> {
            log.info("마피아로 의심되는 사람을 지목한 뒤 투표해 주십시오.");
            messageManager.sendMessage(
                    "/sub/chat/" + roomId,
                    "마피아로 의심되는 사람을 지목한 뒤 투표해 주십시오.",
                    roomId, "admin"
            );
        };
        executor.schedule(task, 2, TimeUnit.SECONDS);


        Runnable endVoteTask = () -> {
            Map<Long, Long> vote = gameSessionVoteService.endVote(roomId, gameSession.getPhaseCount(), gameSession.getGamePhase());
            publishMessage(roomId, vote);
        };

        executor.schedule(endVoteTask, 30, TimeUnit.SECONDS);
    }

    @MessageMapping("/chat/{roomId}/vote")
    private void dayVote(SimpMessageHeaderAccessor accessor,
                         @DestinationVariable Long roomId,
                         @Payload GameSessionVoteRequestDto request) {
        String playerName = getMemberName(accessor);
        Long playerId = gameSessionManager.findMemberByMemberName(playerName).getId();
        log.info("Player {} vote {}", playerName, request.getVote());
        GameSession gameSession = gameSessionManager.findGameByRoomId(roomId);

        Map<Long, Long> voteResult = gameSessionVoteService.vote(roomId, playerId, request);

        if (voteResult == null) {
            throw new CustomException(ErrorCode.FAIL_VOTE);
        }
        else {
            Player voted = playerRedisRepository.findById(voteResult.get(playerId)).get();
            String votedPlayerName = voted.getMemberName();
            log.info("{}가 투표되었습니다.", votedPlayerName);
            messageManager.sendMessage(
                    "/sub/chat/" + roomId,
                    votedPlayerName + "가 투표되었습니다.",
                    roomId, "admin"
            );

            int killCnt = voteResult.entrySet().stream()
                    .filter(e -> e.getValue() != null)
                    .collect(Collectors.toList()).size();

            int alivePlayerCnt = gameSession.getAliveCivilian()
                    + gameSession.getAliveDoctor()
                    + gameSession.getAlivePolice()
                    + gameSession.getAliveMafia();
            log.info("killCnt: {}, alivePlayerCnt: {}", killCnt, alivePlayerCnt);

            if (gameSession.getGamePhase() == GamePhase.DAY_ELIMINATE) {
                if (killCnt > alivePlayerCnt / 2) { // 과반수 이상이 찬성하면
                    Map<Long, Long> vote = gameSessionVoteService.endVote(roomId, gameSession.getPhaseCount(), request.getPhase());
                    publishMessage(roomId, vote);
                }
            }
        }
    }


    @MessageMapping("/pub/chat/{roomId}/nightVote")
    public void nightVote(SimpMessageHeaderAccessor accessor,
                          @DestinationVariable Long roomId,
                          @Payload GameSessionVoteRequestDto request) {
        String playerName = getMemberName(accessor);
        Long playerID = gameSessionManager.findMemberByMemberName(playerName).getId();
        GameRole role = gameSessionManager.findMemberByMemberName(playerName).getMemberGameInfo().getInGameRole();

        log.info("Player {} GameRole: {}", playerName, role);

        if (request.getPhase() != GamePhase.NIGHT_VOTE) {
            throw new CustomException(ErrorCode.GAME_PHASE_NOT_NIGHT_VOTE);
        }

        // 해당 롤의 투표
        Map<Long, Long> voteResult = gameSessionVoteService.nightVote(roomId, playerID, request, role);
        // 관찰자들을 위한 투표 결과 반환
        Map<Long, Long> forObserver = gameSessionVoteService.getVoteResult(roomId, request);

        GameSession gameSession = gameSessionManager.findGameByRoomId(roomId);
        Map<Long, Long> vote = gameSessionVoteService.endVote(roomId, gameSession.getPhaseCount(), gameSession.getGamePhase());
        publishMessage(roomId, vote);

    }

    public String getMemberName(SimpMessageHeaderAccessor accessor) {
        return accessor.getUser().getName().split(",")[1].split(":")[1].trim();
    }

    private void publishMessage(Long roomId, Map<Long, Long> vote) {
        GameSession gameSession = gameSessionManager.findGameByRoomId(roomId);
        log.info("Room {} start Phase {}", roomId, gameSession.getGamePhase());
        if (gameSession.getGamePhase() == GamePhase.DAY_DISCUSSION) {
            DayDiscussionMessage dayDiscussionMessage =
                    new DayDiscussionMessage(roomId, gameSessionVoteService.getSuspiciousList(gameSession, vote));
            dayDiscussionManager.sendMessage(dayDiscussionMessage);
        }
        else if (gameSession.getGamePhase() == GamePhase.DAY_ELIMINATE) {
            DayEliminationMessage dayEliminationMessage =
                    new DayEliminationMessage(roomId, gameSessionVoteService.getEliminationPlayer(gameSession, vote));
            dayEliminationManager.sendMessage(dayEliminationMessage);
        }
    }
}
