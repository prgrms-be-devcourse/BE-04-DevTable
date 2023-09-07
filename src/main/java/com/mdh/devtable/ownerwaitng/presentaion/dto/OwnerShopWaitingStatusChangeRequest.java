package com.mdh.devtable.ownerwaitng.presentaion.dto;

import com.mdh.devtable.waiting.domain.ShopWaitingStatus;

public record OwnerShopWaitingStatusChangeRequest(
        ShopWaitingStatus shopWaitingStatus
) {
}