package com.mdh.devtable.reservation.infra.persistence;

import com.mdh.devtable.reservation.domain.ShopReservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopReservationRepository extends JpaRepository<ShopReservation, Long> {
}
