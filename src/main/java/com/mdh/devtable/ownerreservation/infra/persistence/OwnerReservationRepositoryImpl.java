package com.mdh.devtable.ownerreservation.infra.persistence;

import com.mdh.devtable.reservation.domain.Seat;
import com.mdh.devtable.reservation.domain.ShopReservation;
import com.mdh.devtable.reservation.infra.persistence.SeatRepository;
import com.mdh.devtable.reservation.infra.persistence.ShopReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class OwnerReservationRepositoryImpl implements OwnerReservationRepository {

    private final ShopReservationRepository shopReservationRepository;
    private final SeatRepository seatRepository;

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
}