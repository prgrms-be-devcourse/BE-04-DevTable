package com.mdh.common.reservation.persistence;

import com.mdh.common.reservation.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findAllByShopReservationShopId(Long shopId);
}