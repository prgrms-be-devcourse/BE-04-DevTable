package com.mdh.user.waiting.infra.persistence;

import com.mdh.common.waiting.persistence.WaitingLine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Primary
@Component
@Slf4j
@RequiredArgsConstructor
public class RedisWaitingLine implements WaitingLine {

    private final StringRedisTemplate redisTemplate;

    @Override
    public void save(Long shopId, Long waitingId, LocalDateTime issuedTime) {
        var zSetOperations = redisTemplate.opsForZSet();
        long issuedTimeLong = issuedTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        log.debug("waitingId = {}, issuedDate = {}", waitingId, issuedTimeLong);
        // key score value
        zSetOperations.add(String.valueOf(shopId), String.valueOf(waitingId), issuedTimeLong);
    }

    @Override
    public long findRank(Long shopId, Long waitingId, LocalDateTime issuedTime) {
        var zSetOperations = redisTemplate.opsForZSet();
        Long rank = zSetOperations.rank(String.valueOf(shopId), String.valueOf(waitingId));
        if (rank == null) throw new IllegalArgumentException("매장에 해당 웨이팅이 존재하지 않습니다. waitingId " + waitingId);
        return rank + 1;
    }

    @Override
    public long findTotalWaiting(Long shopId) {
        var zSetOperations = redisTemplate.opsForZSet();
        Long totalWaiting = zSetOperations.size(String.valueOf(shopId));
        if (totalWaiting == null) throw new IllegalArgumentException();
        return totalWaiting;
    }

    @Override
    public void cancel(Long shopId, Long waitingId, LocalDateTime issuedTime) {
        var zSetOperations = redisTemplate.opsForZSet();
        zSetOperations.remove(String.valueOf(shopId), String.valueOf(waitingId));
    }

    @Override
    public void postpone(Long shopId, Long waitingId, LocalDateTime preIssuedTime, LocalDateTime issuedTime) {
        var zSetOperations = redisTemplate.opsForZSet();
        validPostpone(shopId, waitingId, issuedTime);

        long issuedTimeLong = issuedTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        zSetOperations.add(String.valueOf(shopId), String.valueOf(waitingId), issuedTimeLong);
    }

    private void validPostpone(Long shopId, Long waitingId, LocalDateTime issuedTime) {
        if (findRank(shopId, waitingId, issuedTime) == findTotalWaiting(shopId)) {
            throw new IllegalStateException("미루기를 수행 할 수 없는 웨이팅 입니다. " + waitingId);
        }
    }
}