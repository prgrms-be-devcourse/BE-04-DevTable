package com.mdh.devtable.reservation.persistence;

import com.mdh.devtable.reservation.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findAllByShopReservationShopId(Long shopId);
}