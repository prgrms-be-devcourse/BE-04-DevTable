package com.mdh.owner.waiting.presentation.dto;

import com.mdh.common.waiting.domain.ShopWaitingStatus;

public record OwnerShopWaitingStatusChangeRequest(
        ShopWaitingStatus shopWaitingStatus
) {
}