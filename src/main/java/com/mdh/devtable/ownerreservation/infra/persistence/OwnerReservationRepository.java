package com.mdh.devtable.ownerreservation.infra.persistence;

import com.mdh.devtable.reservation.domain.ShopReservation;

public interface OwnerReservationRepository {

    Long saveShopReservation(ShopReservation shopReservation);
}