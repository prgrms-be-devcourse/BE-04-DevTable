package com.mdh.devtable.shop.infra.persistence;

import com.mdh.devtable.shop.domain.Shop;
import com.mdh.devtable.shop.persistence.ShopRepository;
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