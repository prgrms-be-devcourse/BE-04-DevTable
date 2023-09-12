package com.mdh.devtable.ownerreservation.application;

import com.mdh.devtable.ownerreservation.infra.persistence.OwnerReservationRepository;
import com.mdh.devtable.ownerreservation.presentation.dto.SeatCreateRequest;
import com.mdh.devtable.ownerreservation.presentation.dto.ShopReservationCreateRequest;
import com.mdh.devtable.reservation.domain.Seat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class OwnerReservationService {

    private final OwnerReservationRepository ownerReservationRepository;

    @Transactional
    public Long createShopReservation(Long shopId, ShopReservationCreateRequest shopReservationCreateRequest) {
        var shopReservation = shopReservationCreateRequest.toEntity(shopId);
        return ownerReservationRepository.saveShopReservation(shopReservation);
    }

    @Transactional
    public Long saveSeat(Long shopId, SeatCreateRequest seatCreateRequest) {
        var shopReservation = ownerReservationRepository.findShopReservationByShopId(shopId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 매장의 예약정보가 존재하지 않습니다:" + shopId));
        var seat = new Seat(shopReservation, seatCreateRequest.seatType());

        return ownerReservationRepository.saveSeat(seat);
    }
}