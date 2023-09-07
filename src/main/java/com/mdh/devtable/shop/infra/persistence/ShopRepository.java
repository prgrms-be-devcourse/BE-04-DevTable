package com.mdh.devtable.shop.infra.persistence;

import com.mdh.devtable.shop.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopRepository extends JpaRepository<Shop, Long> {
}