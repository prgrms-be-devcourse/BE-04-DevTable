package com.mdh.user.shop.infra;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ShopSearchSortParam {
    PRICE_ASC("price_level_asc"),
    PRICE_DESC("price_level_desc");

    private final String sortParam;

}
