package com.mdh.owner.shop.infra.persistence;


import com.mdh.common.shop.domain.Shop;

public interface OwnerShopRepository {

    Long save(Shop shop);
}