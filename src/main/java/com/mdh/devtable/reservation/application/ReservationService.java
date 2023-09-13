package com.mdh.devtable.reservation.application;

import com.mdh.devtable.reservation.controller.dto.ReservationCreateRequest;
import com.mdh.devtable.reservation.domain.Reservation;
import com.mdh.devtable.reservation.domain.ShopReservation;
import com.mdh.devtable.reservation.domain.ShopReservationDateTimeSeat;
import com.mdh.devtable.reservation.infra.persistence.ReservationRepository;
import com.mdh.devtable.reservation.infra.persistence.ShopReservationDateTimeSeatRepository;
import com.mdh.devtable.reservation.infra.persistence.ShopReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    private final ShopReservationRepository shopReservationRepository;

    private final ShopReservationDateTimeSeatRepository shopReservationDateTimeSeatRepository;

    @Transactional
    public void createReservation(ReservationCreateRequest reservationCreateRequest) {
        var shopId = reservationCreateRequest.shopId();

        var shopReservation = findShopReservation(shopId);
        var shopReservationDateTimeSeats = findShopReservationDateTimeSeats(reservationCreateRequest.shopReservationDateTimeSeatIds());

        var reservation = saveReservation(reservationCreateRequest, shopReservation);
        var size = shopReservationDateTimeSeats.size();
        reservation.validSeatSizeAndPersonCount(size);

        shopReservationDateTimeSeats.forEach(reservation::addShopReservationDateTimeSeats);
    }

    @Transactional
    public String cancelReservation(Long reservationId) {
        var reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NoSuchElementException("등록된 예약이 존재하지 않습니다. id : " + reservationId));

        if (reservation.isCancelShopReservation()) {
            return "정상적으로 예약이 취소되었습니다.";
        }

        return "당일 취소의 경우 패널티가 발생 할 수 있습니다.";
    }

    private Reservation saveReservation(ReservationCreateRequest reservationCreateRequest, ShopReservation shopReservation) {
        var reservation = new Reservation(reservationCreateRequest.userId(),
                shopReservation,
                reservationCreateRequest.requirement(),
                reservationCreateRequest.person_count());
        return reservationRepository.save(reservation);
    }

    private ShopReservation findShopReservation(Long shopId) {
        return shopReservationRepository.findById(shopId)
                .orElseThrow(() -> new NoSuchElementException("매장의 예약 정보가 없습니다. shopId " + shopId));
    }

    private List<ShopReservationDateTimeSeat> findShopReservationDateTimeSeats(List<Long> shopReservationDateTimeSeatIds) {
        var shopReservationDateTimeSeats = shopReservationDateTimeSeatRepository.findAllById(shopReservationDateTimeSeatIds);
        if (shopReservationDateTimeSeats.size() < shopReservationDateTimeSeatIds.size()) {
            throw new NoSuchElementException("예약 좌석 정보들 중 일부가 없습니다.");
        }
        return shopReservationDateTimeSeats;
    }
}