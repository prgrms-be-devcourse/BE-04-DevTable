package com.mdh.user.waiting.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mdh.common.waiting.domain.WaitingPeople;
import com.mdh.common.waiting.domain.WaitingStatus;
import com.mdh.common.waiting.persistence.dto.WaitingDetailsQueryDto;

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

    public WaitingDetailsResponse(WaitingDetailsQueryDto waitingDetailsQueryDto, Integer waitingRank) {
        this(new ShopWaitingResponse(waitingDetailsQueryDto),
                waitingDetailsQueryDto.waitingNumber(),
                waitingRank,
                waitingDetailsQueryDto.waitingStatus(),
                waitingDetailsQueryDto.waitingPeople(),
                waitingDetailsQueryDto.createdDate(),
                waitingDetailsQueryDto.modifiedDate());
    }

    public WaitingDetailsResponse(WaitingDetailsQueryDto waitingDetailsQueryDto) {
        this(waitingDetailsQueryDto, null);
    }
}
