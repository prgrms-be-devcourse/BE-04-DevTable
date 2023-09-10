package com.mdh.devtable.ownerwaiting.presentaion.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OwnerUpdateShopWaitingInfoRequest(

        @NotNull
        Boolean childEnabled,

        @Min(1) @NotNull
        Integer maximumPeople,

        @Min(1) @NotNull
        Integer minimumPeople,

        @Min(1) @NotNull
        Integer maximumWaitingTeam
) {
}