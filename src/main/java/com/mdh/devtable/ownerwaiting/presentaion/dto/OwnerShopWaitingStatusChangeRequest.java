package com.mdh.devtable.ownerwaiting.presentaion.dto;

import com.mdh.devtable.waiting.domain.ShopWaitingStatus;

public record OwnerShopWaitingStatusChangeRequest(
        ShopWaitingStatus shopWaitingStatus
) {
}