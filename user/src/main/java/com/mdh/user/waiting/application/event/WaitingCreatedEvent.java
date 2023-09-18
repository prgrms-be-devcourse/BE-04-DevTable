package com.mdh.user.waiting.application.event;

import com.mdh.common.waiting.domain.Waiting;

public record WaitingCreatedEvent(
        Waiting waiting
) {
}