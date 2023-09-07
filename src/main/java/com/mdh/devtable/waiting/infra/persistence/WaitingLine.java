package com.mdh.devtable.waiting.infra.persistence;

import java.time.LocalDateTime;

public interface WaitingLine {

    void save(Long shopId, Long waitingId, LocalDateTime createdDate);

    int findRank(Long shopId, Long waitingId, LocalDateTime createdDate);

    void cancel(Long shopId, Long waitingId, LocalDateTime createdDate);
}
