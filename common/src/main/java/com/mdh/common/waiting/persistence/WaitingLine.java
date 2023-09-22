package com.mdh.common.waiting.persistence;

import java.time.LocalDateTime;
import java.util.Optional;

public interface WaitingLine {

    void save(Long shopId, Long waitingId, LocalDateTime issuedTime);

    long findRank(Long shopId, Long waitingId, LocalDateTime issuedTime);

    long findTotalWaiting(Long shopId);

    void cancel(Long shopId, Long waitingId, LocalDateTime issuedTime);

    void postpone(Long shopId, Long waitingId, LocalDateTime preIssuedTime, LocalDateTime issuedTime);

    default Optional<Long> visit(Long shopId) {
        throw new IllegalCallerException("해당 메서드를 호출할 수 없습니다.");
    }
}