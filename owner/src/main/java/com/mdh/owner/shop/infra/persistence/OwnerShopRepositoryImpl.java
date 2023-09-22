package com.mdh.owner.shop.infra.persistence;

import com.mdh.common.shop.domain.Shop;
import com.mdh.common.shop.persistence.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class OwnerShopRepositoryImpl implements OwnerShopRepository {

    private final ShopRepository shopRepository;

    @Override
    public Long save(Shop shop) {
        return shopRepository.save(shop).getId();
    }

    @Override
    public Optional<Shop> findShopById(Long ownerId) {
        return shopRepository.findByUserId(ownerId);
    }
}