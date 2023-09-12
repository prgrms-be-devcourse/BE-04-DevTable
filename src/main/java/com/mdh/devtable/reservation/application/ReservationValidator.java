package com.mdh.devtable.reservation.application;

import com.mdh.devtable.reservation.domain.ShopReservation;
import com.mdh.devtable.reservation.domain.ShopReservationDateTimeSeat;
import com.mdh.devtable.reservation.infra.persistence.ShopReservationDateTimeSeatRepository;
import com.mdh.devtable.reservation.infra.persistence.ShopReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Component
public class ReservationValidator {

    private final ShopReservationRepository shopReservationRepository;
    private final ShopReservationDateTimeSeatRepository shopReservationDateTimeSeatRepository;

    public ShopReservation validShopReservation(Long shopId) {
        return shopReservationRepository.findById(shopId)
                .orElseThrow(() -> new NoSuchElementException("매장의 예약 정보가 없습니다. shopId " + shopId));
    }

    public List<ShopReservationDateTimeSeat> validShopReservationDateTimeSeats(List<Long> shopReservationDateTimeSeatIds) {
        return shopReservationDateTimeSeatIds.stream()
                .map(shopReservationDateTimeSeatId ->
                        shopReservationDateTimeSeatRepository.findById(shopReservationDateTimeSeatId)
                                .orElseThrow(() -> new NoSuchElementException("예약 좌석 정보가 없습니다. shopReservationDateTimeSeatId : " + shopReservationDateTimeSeatId)))
                .toList();
    }
}