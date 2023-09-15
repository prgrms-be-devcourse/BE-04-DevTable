package com.mdh.devtable.reservation.persistence;

import com.mdh.devtable.reservation.ShopReservationDateTimeSeat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShopReservationDateTimeSeatRepository extends JpaRepository<ShopReservationDateTimeSeat, Long> {
    List<ShopReservationDateTimeSeat> findAllByShopReservationDateTimeId(Long shopReservationDateTimeId);
}