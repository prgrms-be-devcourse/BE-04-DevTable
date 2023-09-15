package com.mdh.common.waiting.persistence;

import java.time.LocalDateTime;

public interface WaitingLine {

    void save(Long shopId, Long waitingId, LocalDateTime issuedTime);

    int findRank(Long shopId, Long waitingId, LocalDateTime issuedTime);

    int findTotalWaiting(Long shopId);

    void cancel(Long shopId, Long waitingId, LocalDateTime issuedTime);

    void postpone(Long shopId, Long waitingId, LocalDateTime preIssuedTime, LocalDateTime issuedTime);

    boolean isPostpone(Long shopId, Long waitingId, LocalDateTime issuedTime);
}