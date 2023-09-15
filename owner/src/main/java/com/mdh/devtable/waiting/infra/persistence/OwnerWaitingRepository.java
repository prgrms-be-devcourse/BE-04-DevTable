package com.mdh.devtable.waiting.infra.persistence;

import com.mdh.devtable.waiting.domain.ShopWaiting;
import com.mdh.devtable.waiting.domain.Waiting;
import com.mdh.devtable.waiting.domain.WaitingStatus;
import com.mdh.devtable.waiting.persistence.dto.WaitingInfoResponseForOwner;

import java.util.List;
import java.util.Optional;

public interface OwnerWaitingRepository {

    Optional<ShopWaiting> findShopWaitingByShopId(Long shopId);

    Optional<Waiting> findWaitingByWaitingId(Long waitingId);

    List<WaitingInfoResponseForOwner> findWaitingByOwnerIdAndWaitingStatus(Long ownerId, WaitingStatus waitingStatus);

}