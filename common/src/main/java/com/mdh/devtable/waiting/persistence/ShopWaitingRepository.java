package com.mdh.devtable.waiting.persistence;

import com.mdh.devtable.waiting.domain.ShopWaiting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopWaitingRepository extends JpaRepository<ShopWaiting, Long> {

}