package com.mdh.common.waiting.persistence.dto;

import com.mdh.common.shop.domain.ShopDetails;
import com.mdh.common.shop.domain.ShopType;
import com.mdh.common.waiting.domain.WaitingPeople;
import com.mdh.common.waiting.domain.WaitingStatus;

import java.time.LocalDateTime;

public record WaitingDetailsQueryDto(
        Long shopId,
        String shopName,
        ShopType shopType,
        String region,
        ShopDetails shopDetails,
        int waitingNumber,
        WaitingStatus waitingStatus,
        WaitingPeople waitingPeople,
        LocalDateTime createdDate,
        LocalDateTime modifiedDate
) {
}
