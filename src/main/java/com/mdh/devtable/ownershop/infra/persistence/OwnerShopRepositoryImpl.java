package com.mdh.devtable.ownershop.infra.persistence;

import com.mdh.devtable.shop.Shop;
import com.mdh.devtable.shop.infra.persistence.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class OwnerShopRepositoryImpl implements OwnerShopRepository {

    private final ShopRepository shopRepository;

    @Override
    public Long save(Shop shop) {
        return shopRepository.save(shop).getId();
    }
}