package com.mdh.owner.waiting.infra.persistence;

import com.mdh.common.waiting.domain.ShopWaiting;
import com.mdh.common.waiting.domain.Waiting;
import com.mdh.common.waiting.domain.WaitingStatus;
import com.mdh.common.waiting.persistence.dto.WaitingInfoResponseForOwner;

import java.util.List;
import java.util.Optional;

public interface OwnerWaitingRepository {

    Optional<ShopWaiting> findShopWaitingByShopId(Long shopId);

    Optional<Waiting> findWaitingByWaitingId(Long waitingId);

    List<WaitingInfoResponseForOwner> findWaitingByOwnerIdAndWaitingStatus(Long ownerId, WaitingStatus waitingStatus);

}