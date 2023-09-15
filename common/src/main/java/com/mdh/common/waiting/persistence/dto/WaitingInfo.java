package com.mdh.common.waiting.persistence.dto;

import java.time.LocalDateTime;

public record WaitingInfo(
        Long waitingId,
        LocalDateTime waitingStartTime
) {

}