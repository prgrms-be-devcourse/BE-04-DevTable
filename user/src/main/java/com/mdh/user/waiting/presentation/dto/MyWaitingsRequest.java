package com.mdh.user.waiting.presentation.dto;

import com.mdh.common.waiting.domain.WaitingStatus;
import jakarta.validation.constraints.NotNull;

public record MyWaitingsRequest(
        WaitingStatus waitingStatus
) {
}