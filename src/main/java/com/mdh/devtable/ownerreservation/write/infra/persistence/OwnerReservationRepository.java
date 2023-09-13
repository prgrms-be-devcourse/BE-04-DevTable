package com.mdh.devtable.ownerreservation.write.infra.persistence;

import com.mdh.devtable.reservation.domain.*;

import java.util.Optional;

public interface OwnerReservationRepository {

    Long saveShopReservation(ShopReservation shopReservation);

    Optional<ShopReservation> findShopReservationByShopId(Long shopId);

    Long saveSeat(Seat seat);

    Long saveShopReservationDateTime(ShopReservationDateTime shopReservationDateTime);

    Long saveShopReservationDateTimeSeat(ShopReservationDateTimeSeat shopReservationDateTimeSeat);

    Optional<ShopReservationDateTime> findShopReservationDateTimeById(Long shopReservationDateTimeId);

    Optional<Seat> findSeatById(Long seatId);

    Optional<Reservation> findReservationById(Long reservationId);
}