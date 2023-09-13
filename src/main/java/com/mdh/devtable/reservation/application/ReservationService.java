package com.mdh.devtable.reservation.application;

import com.mdh.devtable.reservation.controller.dto.ReservationCreateRequest;
import com.mdh.devtable.reservation.domain.Reservation;
import com.mdh.devtable.reservation.domain.ShopReservation;
import com.mdh.devtable.reservation.infra.persistence.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    private final ReservationValidator reservationValidator;

    @Transactional
    public void createReservation(ReservationCreateRequest reservationCreateRequest) {
        var shopId = reservationCreateRequest.shopId();

        var shopReservation = reservationValidator.validShopReservation(shopId);
        var shopReservationDateTimeSeats = reservationValidator.validShopReservationDateTimeSeats(reservationCreateRequest.shopReservationDateTimeSeatIds());

        var reservation = saveReservation(reservationCreateRequest, shopReservation);
        var size = shopReservationDateTimeSeats.size();
        if (!reservation.isSeatsSizeUnderOrSamePersonCount(size)) {
            throw new IllegalArgumentException("예약하려는 좌석의 수가 예약 인원 수를 초과했습니다. seats size : " + size + ", person count : " + reservation.getPersonCount());
        }

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
}