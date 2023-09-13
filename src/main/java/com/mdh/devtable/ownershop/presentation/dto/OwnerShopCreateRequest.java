package com.mdh.devtable.ownershop.presentation.dto;

import com.mdh.devtable.shop.Shop;
import com.mdh.devtable.shop.ShopType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OwnerShopCreateRequest(

        @NotBlank(message = "상점 이름은 필수입니다.")
        String name,

        @NotBlank(message = "상점 설명은 필수입니다.")
        String description,

        @NotNull(message = "상점 유형은 필수입니다.")
        ShopType shopType,

        @Valid
        ShopDetailsRequest shopDetailsRequest,

        @Valid
        ShopAddressRequest shopAddressRequest,

        @Valid
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