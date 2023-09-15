package com.mdh.devtable.waiting.persistence.dto;

import com.mdh.devtable.shop.domain.ShopDetails;
import com.mdh.devtable.shop.domain.ShopType;
import com.mdh.devtable.waiting.domain.WaitingPeople;
import com.mdh.devtable.waiting.domain.WaitingStatus;

import java.time.LocalDateTime;

public record WaitingDetails(
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
