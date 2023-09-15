package com.mdh.common.waiting.persistence.dto;

public record WaitingInfoResponseForOwner(
        int waitingNumber,
        String phoneNumber
) {
}