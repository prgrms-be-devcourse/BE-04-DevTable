package com.mdh.devtable.waiting.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mdh.devtable.shop.domain.ShopDetails;
import com.mdh.devtable.shop.domain.ShopType;
import com.mdh.devtable.waiting.domain.WaitingPeople;
import com.mdh.devtable.waiting.domain.WaitingStatus;
import com.mdh.devtable.waiting.persistence.dto.WaitingDetails;

import java.time.LocalDateTime;

public record WaitingDetailsResponse(
        ShopWaitingResponse shop,

        int waitingNumber,

        Integer waitingRank,

        WaitingStatus waitingStatus,

        WaitingPeople waitingPeople,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
        LocalDateTime createdDate,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
        LocalDateTime modifiedDate
) {
    public WaitingDetailsResponse(WaitingDetails waitingDetails, Integer waitingRank) {
        this(new ShopWaitingResponse(waitingDetails),
                waitingDetails.waitingNumber(),
                waitingRank,
                waitingDetails.waitingStatus(),
                waitingDetails.waitingPeople(),
                waitingDetails.createdDate(),
                waitingDetails.modifiedDate());
    }

    public WaitingDetailsResponse(WaitingDetails waitingDetails) {
        this(waitingDetails, null);
    }

    //TODO private record로 내부 값 반환 고민
    public record ShopWaitingResponse(
            String shopName,
            ShopType shopType,
            String region,
            //TODO VO DTO 생성
            ShopDetails shopDetails
    ) {
        public ShopWaitingResponse(WaitingDetails waitingDetails) {
            this(waitingDetails.shopName(),
                    waitingDetails.shopType(),
                    waitingDetails.region(),
                    waitingDetails.shopDetails());
        }
    }
}
