package com.mdh.devtable.ownerwaiting.presentaion.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OwnerUpdateShopWaitingInfoRequest(

        @NotNull
        Boolean childEnabled,

        @Min(value = 1, message = "1이상의 값만 입력 해주세요")
        @Max(value = Integer.MAX_VALUE, message = "너무 큰 입력 값입니다.")
        @NotNull(message = "최대 인원 수를 작성해 주세요.")
        Integer maximumPeople,

        @Min(value = 1, message = "1이상의 값만 입력 해주세요")
        @Max(value = Integer.MAX_VALUE, message = "너무 큰 입력 값입니다.")
        @NotNull(message = "최소 인원 수를 작성해 주세요.")
        Integer minimumPeople,

        @Min(value = 1, message = "1이상의 값만 입력 해주세요")
        @Max(value = Integer.MAX_VALUE, message = "너무 큰 입력 값입니다.")
        @NotNull(message = "예약을 받을 최대 팀을 작성해 주세요.")
        Integer maximumWaitingTeam
) {
}