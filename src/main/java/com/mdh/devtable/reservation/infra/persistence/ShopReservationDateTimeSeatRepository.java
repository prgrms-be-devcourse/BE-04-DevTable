package com.mdh.devtable.reservation.infra.persistence;

import com.mdh.devtable.reservation.ShopReservationDateTimeSeat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopReservationDateTimeSeatRepository extends JpaRepository<ShopReservationDateTimeSeat, Long> {
}