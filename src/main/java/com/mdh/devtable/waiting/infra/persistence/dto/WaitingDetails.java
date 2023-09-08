package com.mdh.devtable.waiting.infra.persistence.dto;

import com.mdh.devtable.shop.ShopDetails;
import com.mdh.devtable.shop.ShopType;
import com.mdh.devtable.waiting.application.dto.ShopWaitingResponse;
import com.mdh.devtable.waiting.application.dto.WaitingDetailsResponse;
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
    public WaitingDetailsResponse toWaitingDetailsResponse(Integer waitingRank) {
        var shopWaitingInfo = new ShopWaitingResponse(shopName, shopType, region, shopDetails);
        return new WaitingDetailsResponse(shopWaitingInfo,
                waitingNumber,
                waitingRank,
                waitingStatus,
                waitingPeople,
                createdDate,
                modifiedDate);
    }

    public WaitingDetailsResponse toWaitingDetailsResponse() {
        return toWaitingDetailsResponse(null);
    }
}
