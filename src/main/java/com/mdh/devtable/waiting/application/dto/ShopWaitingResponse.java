package com.mdh.devtable.waiting.application.dto;

import com.mdh.devtable.shop.ShopDetails;
import com.mdh.devtable.shop.ShopType;

public record ShopWaitingResponse(
        String shopName,
        ShopType shopType,
        String region,
        ShopDetails shopDetails
) {
}
