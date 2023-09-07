package com.mdh.devtable.ownerwaitng.infra.persistence;

import com.mdh.devtable.waiting.domain.ShopWaiting;
import com.mdh.devtable.waiting.infra.persistence.ShopWaitingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OwnerWaitingRepositoryImpl implements OwnerWaitingRepository {

    private final ShopWaitingRepository shopWaitingRepository;

    @Override
    public Optional<ShopWaiting> findByShopId(Long shopId) {
        return shopWaitingRepository.findById(shopId);
    }
}