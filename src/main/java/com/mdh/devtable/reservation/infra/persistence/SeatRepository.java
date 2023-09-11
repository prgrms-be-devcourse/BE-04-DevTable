package com.mdh.devtable.reservation.infra.persistence;

import com.mdh.devtable.reservation.domain.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, Long> {
}