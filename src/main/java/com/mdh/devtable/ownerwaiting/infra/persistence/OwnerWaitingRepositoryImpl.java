package com.mdh.devtable.ownerwaiting.infra.persistence;

import com.mdh.devtable.waiting.domain.ShopWaiting;
import com.mdh.devtable.waiting.domain.Waiting;
import com.mdh.devtable.waiting.infra.persistence.ShopWaitingRepository;
import com.mdh.devtable.waiting.infra.persistence.WaitingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OwnerWaitingRepositoryImpl implements OwnerWaitingRepository {

    private final ShopWaitingRepository shopWaitingRepository;
    private final WaitingRepository waitingRepository;

    @Override
    public Optional<ShopWaiting> findShopWaitingByShopId(Long shopId) {
        return shopWaitingRepository.findById(shopId);
    }

    @Override
    public Optional<Waiting> findWaitingByWaitingId(Long waitingId) {
        return waitingRepository.findById(waitingId);
    }
}