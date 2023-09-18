package com.mdh.common.reservation.persistence;

import com.mdh.common.reservation.domain.ShopReservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopReservationRepository extends JpaRepository<ShopReservation, Long> {
}