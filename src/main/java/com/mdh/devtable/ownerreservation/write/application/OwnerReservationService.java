package com.mdh.devtable.ownerreservation.write.application;

import com.mdh.devtable.ownerreservation.write.infra.persistence.OwnerReservationRepository;
import com.mdh.devtable.ownerreservation.write.presentation.dto.SeatCreateRequest;
import com.mdh.devtable.ownerreservation.write.presentation.dto.ShopReservationCreateRequest;
import com.mdh.devtable.ownerreservation.write.presentation.dto.ShopReservationDateTimeCreateRequest;
import com.mdh.devtable.reservation.domain.ReservationStatus;
import com.mdh.devtable.reservation.domain.Seat;
import com.mdh.devtable.reservation.domain.ShopReservationDateTime;
import com.mdh.devtable.reservation.domain.ShopReservationDateTimeSeat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class OwnerReservationService {

    private final OwnerReservationRepository ownerReservationRepository;

    private final OwnerReservationServiceValidator ownerReservationServiceValidator;

    @Transactional
    public Long createShopReservation(Long shopId, ShopReservationCreateRequest shopReservationCreateRequest) {
        var shopReservation = shopReservationCreateRequest.toEntity(shopId);
        return ownerReservationRepository.saveShopReservation(shopReservation);
    }

    @Transactional
    public Long saveSeat(Long shopId, SeatCreateRequest seatCreateRequest) {
        var shopReservation = ownerReservationRepository.findShopReservationByShopId(shopId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 매장의 예약정보가 존재하지 않습니다:" + shopId));
        var seat = new Seat(shopReservation, seatCreateRequest.count(), seatCreateRequest.seatType());

        return ownerReservationRepository.saveSeat(seat);
    }

    @Transactional
    public Long createShopReservationDateTime(Long shopId, ShopReservationDateTimeCreateRequest shopReservationDateTimeCreateRequest) {
        var shopReservation = ownerReservationRepository.findShopReservationByShopId(shopId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 매장의 예약정보가 존재하지 않습니다:" + shopId));
        var shopReservationDateTime = new ShopReservationDateTime(shopReservation,
                shopReservationDateTimeCreateRequest.localDate(),
                shopReservationDateTimeCreateRequest.localTime());

        return ownerReservationRepository.saveShopReservationDateTime(shopReservationDateTime);
    }

    @Transactional
    public Long createShopReservationDateTimeSeat(Long shopReservationDateTimeId, Long seatId) {
        ownerReservationServiceValidator.validateCreateShopReservationDateTimeSeat(shopReservationDateTimeId, seatId);

        var shopReservationDateTime = ownerReservationRepository.findShopReservationDateTimeById(shopReservationDateTimeId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 매장의 예약 날짜 정보가 없습니다.: " + shopReservationDateTimeId));
        var seat = ownerReservationRepository.findSeatById(seatId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 좌석 정보가 없습니다.: " + seatId));

        var shopReservationDateTimeSeat = new ShopReservationDateTimeSeat(shopReservationDateTime, seat);

        return ownerReservationRepository.saveShopReservationDateTimeSeat(shopReservationDateTimeSeat);
    }

    @Transactional
    public void cancelReservationByOwner(Long reservationId) {
        var reservation = ownerReservationRepository.findReservationById(reservationId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 예약 정보가 없습니다.: " + reservationId));
        reservation.updateReservationStatus(ReservationStatus.CANCEL);
    }

    @Transactional
    public void markReservationAsVisitedByOwner(Long reservationId) {
        var reservation = ownerReservationRepository.findReservationById(reservationId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 예약 정보가 없습니다.: " + reservationId));
        reservation.updateReservationStatus(ReservationStatus.VISITED);
    }

    @Transactional
    public void markReservationAsNoShowByOwner(Long reservationId) {
        var reservation = ownerReservationRepository.findReservationById(reservationId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 예약 정보가 없습니다.: " + reservationId));
        reservation.updateReservationStatus(ReservationStatus.NO_SHOW);
    }
}