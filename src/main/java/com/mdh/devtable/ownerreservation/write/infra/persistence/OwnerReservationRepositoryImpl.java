package com.mdh.devtable.ownerreservation.write.infra.persistence;

import com.mdh.devtable.reservation.domain.*;
import com.mdh.devtable.reservation.infra.persistence.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class OwnerReservationRepositoryImpl implements OwnerReservationRepository {

    private final ShopReservationRepository shopReservationRepository;
    private final SeatRepository seatRepository;
    private final ShopReservationDateTimeRepository shopReservationDateTimeRepository;
    private final ShopReservationDateTimeSeatRepository shopReservationDateTimeSeatRepository;
    private final ReservationRepository reservationRepository;

    @Override
    public Long saveShopReservation(ShopReservation shopReservation) {
        return shopReservationRepository.save(shopReservation).getShopId();
    }

    @Override
    public Optional<ShopReservation> findShopReservationByShopId(Long shopId) {
        return shopReservationRepository.findById(shopId);
    }

    @Override
    public Long saveSeat(Seat seat) {
        return seatRepository.save(seat).getId();
    }

    @Override
    public Long saveShopReservationDateTime(ShopReservationDateTime shopReservationDateTime) {
        return shopReservationDateTimeRepository.save(shopReservationDateTime).getId();
    }

    @Override
    public Long saveShopReservationDateTimeSeat(ShopReservationDateTimeSeat shopReservationDateTimeSeat) {
        return shopReservationDateTimeSeatRepository.save(shopReservationDateTimeSeat).getId();
    }

    @Override
    public Optional<ShopReservationDateTime> findShopReservationDateTimeById(Long shopReservationDateTimeId) {
        return shopReservationDateTimeRepository.findById(shopReservationDateTimeId);
    }

    @Override
    public Optional<Seat> findSeatById(Long seatId) {
        return seatRepository.findById(seatId);
    }

    @Override
    public Optional<Reservation> findReservationById(Long reservationId) {
        return reservationRepository.findById(reservationId);
    }
}