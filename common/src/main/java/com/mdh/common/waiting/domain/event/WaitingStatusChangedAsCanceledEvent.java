package com.mdh.common.waiting.domain.event;

import com.mdh.common.waiting.domain.Waiting;

public record WaitingStatusChangedAsCanceledEvent(
        Waiting waiting
) {
}