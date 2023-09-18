package com.mdh.common.reservation.persistence;

import com.mdh.common.reservation.domain.ShopReservationDateTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopReservationDateTimeRepository extends JpaRepository<ShopReservationDateTime, Long> {
}