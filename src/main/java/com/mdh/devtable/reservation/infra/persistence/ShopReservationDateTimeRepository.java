package com.mdh.devtable.reservation.infra.persistence;

import com.mdh.devtable.reservation.ShopReservationDateTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopReservationDateTimeRepository extends JpaRepository<ShopReservationDateTime, Long> {
}