package com.mdh.devtable.ownerreservation.infra.persistence;

import com.mdh.devtable.reservation.domain.Seat;
import com.mdh.devtable.reservation.domain.ShopReservation;

import java.util.Optional;

public interface OwnerReservationRepository {

    Long saveShopReservation(ShopReservation shopReservation);

    Optional<ShopReservation> findShopReservationByShopId(Long shopId);

    Long saveSeat(Seat seat);
}