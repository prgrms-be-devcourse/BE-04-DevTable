package com.mdh.devtable.waiting.persistence.dto;

import java.time.LocalDateTime;

public record WaitingInfo(
        Long waitingId,
        LocalDateTime waitingStartTime
) {

}