package com.mdh.devtable.ownerwaiting.presentaion.dto;

import com.mdh.devtable.waiting.domain.WaitingStatus;

public record WaitingInfoRequestForOwner(
        WaitingStatus waitingStatus
) {
}