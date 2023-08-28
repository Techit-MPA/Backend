package com.springles.repository;

import com.springles.domain.constants.GamePhase;
import com.springles.domain.entity.Vote;
import lombok.Getter;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Getter
public class VoteRedisRepository {
    // RedisTemplate: Redis 데이터베이스와 상호작용하기 위한 편리한 방법을 제공하는 도구
    private final RedisTemplate<String, Vote> redisTemplate;
    // HashOperations: Redis 해시에 데이터를 추가하고 조회하고 수정하고 삭제하는 작업을 간편하게 수행
    private HashOperations<String, Long, Vote> opsHashVote;
    private static final String key = "Vote";

    // 의존성 주입하고 초기 세팅
    public VoteRedisRepository(RedisTemplate<String, Vote> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.opsHashVote = redisTemplate.opsForHash();
    }

    // 투표 생성을 위해 <"Vote", playerId, Vote> 형태로 Vote는 비워 둔 채 hash 저드
    public Map<Long, Vote> startVote(List<Long> players, GamePhase phase) {
        Map<Long, Vote> voteResult = new HashMap<Long, Vote>();
        players.forEach((playerId) -> {
            Vote vote;
            if(!isExist(playerId)) {
                // 존재하지 않으면 새로 생성
                vote = Vote.builder(playerId, phase);
            } else {
                // 존재하면 가져오기
                vote = getVote(playerId);
            }
            updateVote(playerId, vote);
        });
        return voteResult;
    }

    // playerId에 해당하는 사용자가 남긴 투표가 존재하는지 검색
    public boolean isExist(Long playerId) {
        return opsHashVote.hasKey(key, playerId);
    }

    // playerId에 해당하는 사용자가 남긴 투표 반환
    public Vote getVote(Long playerId) {
        return opsHashVote.get(key, playerId);
    }

    // Vote를 받아서 업데이트하는 함수
    private void updateVote(Long playerId, Vote vote) {
        opsHashVote.put(key, playerId, vote);
    }

    private void deleteVote(Long playerId) {
        opsHashVote.delete(key, playerId);
    }


//    playerId: 1,
//    Vote: {
//        playerId
//        GamePhase
//        vote
//        confirm
//    }
//    로 된 리스트 반환해 주는 함수
    public Map<Long, Vote> getVoteResult(List<Long> players) {
        Map<Long, Vote> voteResult = new HashMap<Long, Vote>();
        players.forEach(playerId -> {
            voteResult.put(playerId, getVote(playerId));
        });
        return voteResult;
    }

    public boolean vote(Long playerId, Long player) {
        // 투표를 시작할 때 Vote 객체 다 만들고 초기화했으니까 새로 생성하지 않고 불러들임
        Vote vote = getVote(playerId);
        if (!vote.isConfirm()) {
            vote.setConfirm(true); // 투표를 확정하고
            updateVote(playerId, vote); // playerId가 vote 투표를 헀다고 업데이트
            return true;
        }
        return false;
    }
}
