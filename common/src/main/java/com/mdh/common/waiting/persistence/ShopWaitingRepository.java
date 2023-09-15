package com.mdh.common.waiting.persistence;

import com.mdh.common.waiting.domain.ShopWaiting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopWaitingRepository extends JpaRepository<ShopWaiting, Long> {

}