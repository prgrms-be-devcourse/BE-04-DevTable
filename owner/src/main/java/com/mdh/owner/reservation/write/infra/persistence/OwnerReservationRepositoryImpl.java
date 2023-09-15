package com.mdh.owner.reservation.write.infra.persistence;

import com.mdh.common.reservation.*;
import com.mdh.common.reservation.persistence.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
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
    public Optional<Reservation> findReservationById(Long reservationId) {
        return reservationRepository.findById(reservationId);
    }

    @Override
    public List<Seat> findAllSeatsByShopId(Long shopId) {
        return seatRepository.findAllByShopReservationShopId(shopId);
    }

    @Override
    public void saveAllShopReservationDateTimeSeat(Iterable<ShopReservationDateTimeSeat> shopReservationDateTimeSeats) {
        shopReservationDateTimeSeatRepository.saveAll(shopReservationDateTimeSeats);
    }
}