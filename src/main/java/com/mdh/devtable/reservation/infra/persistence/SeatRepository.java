package com.mdh.devtable.reservation.infra.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<SeatRepository, Long> {
}