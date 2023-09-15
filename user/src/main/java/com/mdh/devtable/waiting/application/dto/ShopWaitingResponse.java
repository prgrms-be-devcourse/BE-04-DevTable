package com.mdh.devtable.waiting.application.dto;


import com.mdh.devtable.shop.domain.ShopDetails;
import com.mdh.devtable.shop.domain.ShopType;

public record ShopWaitingResponse(
        String shopName,
        ShopType shopType,
        String region,
        ShopDetails shopDetails
) {
}
