package com.mdh.devtable.ownerwaitng.infra.persistence;


import com.mdh.devtable.waiting.domain.ShopWaiting;
import com.mdh.devtable.waiting.domain.Waiting;

import java.util.Optional;

public interface OwnerWaitingRepository {

    Optional<ShopWaiting> findShopWaitingByShopId(Long shopId);

    Optional<Waiting> findWaitingByWaitingId(Long waitingId);

}