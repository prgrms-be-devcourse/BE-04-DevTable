package com.mdh.common.shop.persistence;

import com.mdh.common.shop.domain.Region;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionRepository extends JpaRepository<Region, Long> {
}
