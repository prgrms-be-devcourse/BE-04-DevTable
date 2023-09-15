package com.mdh.owner.reservation.write.infra.persistence;

import com.mdh.common.reservation.*;

import java.util.List;
import java.util.Optional;

public interface OwnerReservationRepository {

    Long saveShopReservation(ShopReservation shopReservation);

    Optional<ShopReservation> findShopReservationByShopId(Long shopId);

    Long saveSeat(Seat seat);

    Long saveShopReservationDateTime(ShopReservationDateTime shopReservationDateTime);


    Optional<Reservation> findReservationById(Long reservationId);

    List<Seat> findAllSeatsByShopId(Long shopId);

    void saveAllShopReservationDateTimeSeat(Iterable<ShopReservationDateTimeSeat> shopReservationDateTimeSeats);
}