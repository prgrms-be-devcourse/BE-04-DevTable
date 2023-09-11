package com.mdh.devtable.ownerwaiting.presentaion.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OwnerUpdateShopWaitingInfoRequest(

        @NotNull
        Boolean childEnabled,

        @Min(value = 1, message = "1이상의 값만 입력 해주세요")
        @Max(value = Integer.MAX_VALUE, message = "너무 큰 입력 값입니다.")
        @NotNull
        Integer maximumPeople,

        @Min(value = 1, message = "1이상의 값만 입력 해주세요")
        @Max(value = Integer.MAX_VALUE, message = "너무 큰 입력 값입니다.")
        @NotNull
        Integer minimumPeople,

        @Min(value = 1, message = "1이상의 값만 입력 해주세요")
        @Max(value = Integer.MAX_VALUE, message = "너무 큰 입력 값입니다.")
        @NotNull
        Integer maximumWaitingTeam
) {
}