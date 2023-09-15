package com.mdh.devtable.shop.persistence;

import com.mdh.devtable.shop.domain.Region;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionRepository extends JpaRepository<Region, Long> {
}
