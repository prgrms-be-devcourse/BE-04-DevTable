package com.mdh.devtable.waiting.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mdh.devtable.waiting.domain.WaitingPeople;
import com.mdh.devtable.waiting.domain.WaitingStatus;

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
}
