package com.mdh.user.shop.application.dto;

import com.mdh.common.shop.domain.ShopType;
import com.mdh.common.shop.persistence.dto.ShopQueryDto;

public record ShopResponse(
        Long shopId,
        String shopName,
        ShopType shopType,
        int minPrice,
        int maxPrice,
        String city,
        String district,
        int totalWaitingCount
) {
    public ShopResponse(ShopQueryDto shopQueryDto, int totalWaitingCount) {
        this(
                shopQueryDto.shopId(),
                shopQueryDto.shopName(),
                shopQueryDto.shopType(),
                getMinPrice(shopQueryDto),
                getMaxPrice(shopQueryDto),
                shopQueryDto.city(),
                shopQueryDto.district(),
                totalWaitingCount);
    }

    private static int getMinPrice(ShopQueryDto shopQueryDto) {
        return Math.min(shopQueryDto.lunchMinPrice(), shopQueryDto.dinnerMinPrice());
    }

    private static int getMaxPrice(ShopQueryDto shopQueryDto) {
        return Math.max(shopQueryDto.lunchMaxPrice(), shopQueryDto.dinnerMaxPrice());
    }
}
