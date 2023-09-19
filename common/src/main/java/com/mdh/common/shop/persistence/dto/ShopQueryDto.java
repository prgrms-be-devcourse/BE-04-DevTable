package com.mdh.common.shop.persistence.dto;

import com.mdh.common.shop.domain.ShopType;

public record ShopQueryDto(
        Long shopId,
        String shopName,
        ShopType shopType,
        int lunchMinPrice,
        int lunchMaxPrice,
        int dinnerMinPrice,
        int dinnerMaxPrice,
        String city,
        String district
) {
}
