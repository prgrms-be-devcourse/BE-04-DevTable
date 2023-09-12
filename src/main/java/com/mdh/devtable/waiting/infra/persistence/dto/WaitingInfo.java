package com.mdh.devtable.waiting.infra.persistence.dto;

import java.time.LocalDateTime;

public record WaitingInfo(
        Long waitingId,
        LocalDateTime waitingStartTime
) {
    
}