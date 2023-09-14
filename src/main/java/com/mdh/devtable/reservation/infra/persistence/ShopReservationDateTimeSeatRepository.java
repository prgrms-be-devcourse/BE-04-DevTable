package com.mdh.devtable.reservation.infra.persistence;

import com.mdh.devtable.reservation.domain.ShopReservationDateTimeSeat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShopReservationDateTimeSeatRepository extends JpaRepository<ShopReservationDateTimeSeat, Long> {
    List<ShopReservationDateTimeSeat> findAllByShopReservationDateTimeId(Long shopReservationDateTimeId);
}