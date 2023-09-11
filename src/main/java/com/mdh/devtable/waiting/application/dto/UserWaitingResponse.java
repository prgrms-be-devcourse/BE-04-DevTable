package com.mdh.devtable.waiting.application.dto;

public record UserWaitingResponse(
        Long shopId,
        Long waitingId,
        String shopName,
        String shopType,
        String region,
        int waitingNumber,
        int waitingCount
) {
}
