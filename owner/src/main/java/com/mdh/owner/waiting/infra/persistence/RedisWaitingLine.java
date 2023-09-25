package com.mdh.owner.waiting.infra.persistence;

import com.mdh.common.waiting.persistence.WaitingLine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Primary
@Component
@Slf4j
@RequiredArgsConstructor
public class RedisWaitingLine implements WaitingLine {

    private final StringRedisTemplate redisTemplate;
    private static final int RANK_ADD_NUMBER = 1;

    @Override
    public void save(Long shopId, Long waitingId, LocalDateTime issuedTime) {
        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                try {
                    operations.multi();
                    var zSetOperations = redisTemplate.opsForZSet();
                    long issuedTimeLong = issuedTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                    log.debug("waitingId = {}, issuedDate = {}", waitingId, issuedTimeLong);
                    // key score value
                    zSetOperations.add(String.valueOf(shopId), String.valueOf(waitingId), issuedTimeLong);
                } catch (Exception e) {
                    operations.discard();
                }
                return operations.exec();
            }
        });
    }

    @Override
    public Optional<Long> findRank(Long shopId, Long waitingId, LocalDateTime issuedTime) {
        var zSetOperations = redisTemplate.opsForZSet();
        var rank = zSetOperations.rank(String.valueOf(shopId), String.valueOf(waitingId));
        return Optional.ofNullable(rank + RANK_ADD_NUMBER);
    }

    @Override
    public long findTotalWaiting(Long shopId) {
        var zSetOperations = redisTemplate.opsForZSet();
        var totalWaiting = zSetOperations.size(String.valueOf(shopId));
        log.info("totalWaiting = {}", totalWaiting);
        if (totalWaiting == null) {
            throw new IllegalArgumentException("해당 매장의 웨이팅 라인이 존재하지 않습니다. shopId : " + shopId);
        }
        return totalWaiting;
    }

    @Override
    public void cancel(Long shopId, Long waitingId, LocalDateTime issuedTime) {
        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                try {
                    operations.multi();
                    var zSetOperations = redisTemplate.opsForZSet();
                    zSetOperations.remove(String.valueOf(shopId), String.valueOf(waitingId));
                } catch (Exception e) {
                    operations.discard();
                }
                return operations.exec();
            }
        });
    }

    @Override
    public void postpone(Long shopId, Long waitingId, LocalDateTime preIssuedTime, LocalDateTime issuedTime) {
        validPostpone(shopId, waitingId, issuedTime);
        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                try {
                    operations.multi();
                    long issuedTimeLong = issuedTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                    redisTemplate.opsForZSet().add(String.valueOf(shopId), String.valueOf(waitingId), issuedTimeLong);
                } catch (Exception e) {
                    log.debug("웨이팅을 미뤘을 때 에러가 발생했습니다. ", e);
                    operations.discard();
                }
                return operations.exec();
            }
        });
    }

    private void validPostpone(Long shopId, Long waitingId, LocalDateTime issuedTime) {
        var rank = findRank(shopId, waitingId, issuedTime).orElseThrow(() -> new IllegalStateException("매장에 해당 웨이팅이 없습니다"));
        if (rank == findTotalWaiting(shopId)) {
            throw new IllegalStateException("미루기를 수행 할 수 없는 웨이팅 입니다. " + waitingId);
        }
    }

    @Override
    public Optional<Long> visit(Long shopId) {
        var zSetOperations = redisTemplate.opsForZSet();
        var firstValue = Optional.ofNullable(zSetOperations.popMin(String.valueOf(shopId)))
                .map(first -> first.getValue())
                .orElseThrow(() -> new IllegalCallerException("파이프라인이나 트랜잭션에서 호출 시 null을 반환합니다."));

        return Optional.of(Long.valueOf(firstValue));
    }
}