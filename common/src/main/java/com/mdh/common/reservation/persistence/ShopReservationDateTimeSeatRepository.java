package com.mdh.common.reservation.persistence;

import com.mdh.common.reservation.domain.ShopReservationDateTimeSeat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShopReservationDateTimeSeatRepository extends JpaRepository<ShopReservationDateTimeSeat, Long> {
    List<ShopReservationDateTimeSeat> findAllByShopReservationDateTimeId(Long shopReservationDateTimeId);
}