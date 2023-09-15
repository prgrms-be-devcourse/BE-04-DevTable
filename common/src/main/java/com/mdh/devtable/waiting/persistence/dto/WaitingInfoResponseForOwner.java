package com.mdh.devtable.waiting.persistence.dto;

public record WaitingInfoResponseForOwner(
        int waitingNumber,
        String phoneNumber
) {
}