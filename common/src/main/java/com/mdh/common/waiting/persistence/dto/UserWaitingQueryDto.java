package com.mdh.common.waiting.persistence.dto;


import com.mdh.common.shop.domain.ShopType;

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
}
