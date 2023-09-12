package com.mdh.devtable.ownershop.infra.persistence;

import com.mdh.devtable.shop.Shop;

public interface OwnerShopRepository {

    Long save(Shop shop);
}