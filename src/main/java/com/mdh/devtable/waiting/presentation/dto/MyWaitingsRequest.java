package com.mdh.devtable.waiting.presentation.dto;

import com.mdh.devtable.waiting.domain.WaitingStatus;
import jakarta.validation.constraints.NotNull;

public record MyWaitingsRequest(
        @NotNull(message = "userId는 null 일 수 없습니다.")
        Long userId,
        WaitingStatus waitingStatus
) {
}
