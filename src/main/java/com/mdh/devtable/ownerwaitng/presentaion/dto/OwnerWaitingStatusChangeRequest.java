package com.mdh.devtable.ownerwaitng.presentaion.dto;

import com.mdh.devtable.waiting.domain.WaitingStatus;

public record OwnerWaitingStatusChangeRequest(
        WaitingStatus waitingStatus
) {
}