package com.mdh.common.shop.persistence;

import com.mdh.common.shop.domain.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Long>, ShopRepositoryCustom {

    Optional<Shop> findByUserId(Long ownerId);
}