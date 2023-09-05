package com.mdh.devtable.ownerwaitng.infra.persistence;

import com.mdh.devtable.waiting.ShopWaiting;

import java.util.Optional;

public interface OwnerWaitingRepository {

    Optional<ShopWaiting> findByShopId(Long shopId);
}