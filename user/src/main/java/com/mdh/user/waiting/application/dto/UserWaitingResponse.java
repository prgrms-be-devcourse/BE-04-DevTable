package com.mdh.user.waiting.application.dto;

import com.mdh.common.waiting.persistence.dto.UserWaitingQueryDto;

public record UserWaitingResponse(
        Long shopId,
        Long waitingId,
        String shopName,
        String shopType,
        String region,
        int waitingNumber,
        int waitingCount
) {

    public UserWaitingResponse(UserWaitingQueryDto dto) {
        this(dto.shopId(),
                dto.waitingId(),
                dto.shopName(),
                dto.shopType().getName(),
                fieldToRegion(dto),
                dto.waitingNumber(),
                totalWaitingCount(dto));
    }

    private static String fieldToRegion(UserWaitingQueryDto dto) {
        StringBuilder sb = new StringBuilder();
        sb.append(dto.city())
                .append(" ")
                .append(dto.district());

        return sb.toString();
    }

    private static int totalWaitingCount(UserWaitingQueryDto dto) {
        return dto.adultCount() + dto.childCount();
    }
}
