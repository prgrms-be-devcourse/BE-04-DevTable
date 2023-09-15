package com.mdh.devtable.waiting.presentation.dto;

import com.mdh.devtable.waiting.domain.WaitingStatus;

public record WaitingInfoRequestForOwner(
        WaitingStatus waitingStatus
) {
}