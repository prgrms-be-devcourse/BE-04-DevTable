package com.mdh.devtable.shop.infra.persistence;


import com.mdh.devtable.shop.domain.Shop;

public interface OwnerShopRepository {

    Long save(Shop shop);
}