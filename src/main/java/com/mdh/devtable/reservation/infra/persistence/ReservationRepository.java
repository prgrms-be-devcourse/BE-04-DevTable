package com.mdh.devtable.reservation.infra.persistence;

import com.mdh.devtable.reservation.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
