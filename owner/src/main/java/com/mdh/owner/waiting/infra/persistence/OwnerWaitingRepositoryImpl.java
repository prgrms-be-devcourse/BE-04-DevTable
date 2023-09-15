package com.mdh.owner.waiting.infra.persistence;

import com.mdh.common.waiting.domain.ShopWaiting;
import com.mdh.common.waiting.domain.Waiting;
import com.mdh.common.waiting.domain.WaitingStatus;
import com.mdh.common.waiting.persistence.ShopWaitingRepository;
import com.mdh.common.waiting.persistence.WaitingRepository;
import com.mdh.common.waiting.persistence.dto.WaitingInfoResponseForOwner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    @Override
    public List<WaitingInfoResponseForOwner> findWaitingByOwnerIdAndWaitingStatus(Long ownerId, WaitingStatus waitingStatus) {
        return waitingRepository.findWaitingByOwnerIdAndWaitingStatus(ownerId, waitingStatus);
    }
}