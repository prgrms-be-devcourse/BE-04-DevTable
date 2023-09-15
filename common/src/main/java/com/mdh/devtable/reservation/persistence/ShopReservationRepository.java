package com.mdh.devtable.reservation.persistence;

import com.mdh.devtable.reservation.ShopReservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopReservationRepository extends JpaRepository<ShopReservation, Long> {
}
