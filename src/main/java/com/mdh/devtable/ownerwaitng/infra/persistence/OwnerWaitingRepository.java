package com.mdh.devtable.ownerwaitng.infra.persistence;


import com.mdh.devtable.waiting.domain.ShopWaiting;

import java.util.Optional;

public interface OwnerWaitingRepository {

    Optional<ShopWaiting> findByShopId(Long shopId);
}