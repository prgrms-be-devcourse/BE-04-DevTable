package com.mdh.devtable.waiting.infra.persistence;

import com.mdh.devtable.waiting.ShopWaiting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopWaitingRepository extends JpaRepository<ShopWaiting, Long> {
}