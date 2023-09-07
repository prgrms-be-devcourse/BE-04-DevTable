package com.mdh.devtable.shop.infra.persistence;

import com.mdh.devtable.shop.Region;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionRepository extends JpaRepository<Region, Long> {
}
