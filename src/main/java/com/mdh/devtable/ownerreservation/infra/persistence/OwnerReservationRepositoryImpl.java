package com.mdh.devtable.ownerreservation.infra.persistence;

import com.mdh.devtable.reservation.domain.ShopReservation;
import com.mdh.devtable.reservation.infra.persistence.ShopReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class OwnerReservationRepositoryImpl implements OwnerReservationRepository {

    private final ShopReservationRepository shopReservationRepository;

    @Override
    public Long saveShopReservation(ShopReservation shopReservation) {
        return shopReservationRepository.save(shopReservation).getShopId();
    }
}