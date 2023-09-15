package com.mdh.devtable.shop.persistence;

import com.mdh.devtable.shop.domain.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopRepository extends JpaRepository<Shop, Long> {
}