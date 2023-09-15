package com.mdh.devtable.waiting.presentation.dto;

import com.mdh.devtable.waiting.domain.ShopWaitingStatus;

public record OwnerShopWaitingStatusChangeRequest(
        ShopWaitingStatus shopWaitingStatus
) {
}