package com.mdh.devtable.waiting.infra.persistence.dto;

import com.mdh.devtable.shop.ShopType;
import com.mdh.devtable.waiting.application.dto.UserWaitingResponse;

public record UserWaitingQueryDto(
        Long shopId,
        Long waitingId,
        String shopName,
        ShopType shopType,
        String city,
        String district,
        int waitingNumber,
        int adultCount,
        int childCount
) {

    public UserWaitingResponse toUserWaitingResponse() {
        return new UserWaitingResponse(
                shopId,
                waitingId,
                shopName,
                shopType.getName(),
                fieldToRegion(),
                waitingNumber,
                adultCount + childCount
        );
    }

    private String fieldToRegion() {
        StringBuilder sb = new StringBuilder();
        sb.append(city)
                .append(" ")
                .append(district);

        return sb.toString();
    }
}
