package com.mdh.devtable.ownershop.presentation.dto;

import com.mdh.devtable.shop.Shop;
import com.mdh.devtable.shop.ShopType;

public record OwnerShopCreateRequest(
        String name,
        String description,
        ShopType shopType,
        ShopDetailsRequest shopDetailsRequest,
        ShopAddressRequest shopAddressRequest,
        RegionRequest regionRequest

) {
    public Shop toEntity(Long userId) {
        return Shop.builder()
                .userId(userId)
                .name(name)
                .description(description)
                .shopDetails(shopDetailsRequest.toVO())
                .shopAddress(shopAddressRequest.toVO())
                .shopType(shopType)
                .region(regionRequest.toEntity())
                .build();
    }
}