package com.mdh.common.shop.persistence.dto;

import com.mdh.common.shop.domain.ShopPrice;
import com.mdh.common.shop.domain.ShopType;

public record ReservationShopSearchQueryDto(
        Long id,
        String name,
        String description,
        ShopType shopType,
        String city,
        String district,
        ShopPrice shopPrice,
        int availableSeatCount
) {
}
