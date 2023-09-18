package com.mdh.common.shop.persistence.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ShopSearchCondParam {
    NAME("name"),
    SHOP_TYPE("shop_type"),
    REGION("region"),
    MIN_PRICE("min_price"),
    MAX_PRICE("max_price"),
    SORT("sort_type");

    private final String paramName;
}
