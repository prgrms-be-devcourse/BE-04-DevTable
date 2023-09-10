package com.mdh.devtable.ownerwaiting.presentaion.dto;

public record OwnerUpdateShopWaitingInfoRequest(
        Boolean childEnabled,
        Integer maximumPeople,
        Integer minimumPeople,
        Integer maximumWaitingTeam
) {
}