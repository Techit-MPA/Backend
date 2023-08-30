package com.springles.service;

import com.springles.domain.constants.GamePhase;
import com.springles.domain.constants.GameRole;
import com.springles.domain.dto.vote.GameSessionVoteRequestDto;

import java.time.LocalDateTime;
import java.util.Map;

public interface GameSessionVoteService {
    void startVote (Long roomId, int phaseCount, GamePhase phase, LocalDateTime time, Map<Long, GameRole> players);
    void endVote (Long roomId, int phaseCount, GamePhase phase);
    Map<Long, Long> vote (Long roomId, Long playerId, GameSessionVoteRequestDto request);
    Map<Long, Long> nightVote (Long roomId, Long playerId, GameSessionVoteRequestDto request, GameRole role);
    Map<Long, Boolean> confirmVote (Long roomId, Long playerId, GameSessionVoteRequestDto request);
    Map<Long, Boolean> getConfirm(Long roomId, Long playerId, GameSessionVoteRequestDto request);
    Map<Long, Boolean> getNightConfirm(Long roomId, Long playerId, GameSessionVoteRequestDto request, GameRole role);
    Map<Long, Long> getVoteResult(Long roomId, GameSessionVoteRequestDto request);
}
