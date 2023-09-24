package com.mdh.owner.shop.infra.persistence;


import com.mdh.common.shop.domain.Shop;

import java.util.Optional;

public interface OwnerShopRepository {

    Long save(Shop shop);

    Optional<Shop> findShopByOwnerId(Long ownerId);

    Optional<Shop> findShopById(Long id);
}