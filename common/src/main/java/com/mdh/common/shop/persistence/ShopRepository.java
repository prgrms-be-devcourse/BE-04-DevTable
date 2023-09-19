package com.mdh.common.shop.persistence;

import com.mdh.common.shop.domain.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopRepository extends JpaRepository<Shop, Long>, ShopQueryRepository {
}