package com.mdh.owner.reservation.write.application;

import com.mdh.common.reservation.domain.ReservationStatus;
import com.mdh.common.reservation.domain.Seat;
import com.mdh.common.reservation.domain.ShopReservationDateTime;
import com.mdh.common.reservation.domain.ShopReservationDateTimeSeat;
import com.mdh.owner.reservation.write.infra.persistence.OwnerReservationRepository;
import com.mdh.owner.reservation.write.presentation.dto.SeatCreateRequest;
import com.mdh.owner.reservation.write.presentation.dto.ShopReservationCreateRequest;
import com.mdh.owner.reservation.write.presentation.dto.ShopReservationDateTimeCreateRequest;
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
        var shopReservation = ownerReservationRepository.findShopReservationByShopId(shopId).orElseThrow(() -> new NoSuchElementException("해당 ID의 매장의 예약정보가 존재하지 않습니다:" + shopId));
        var seat = new Seat(shopReservation, seatCreateRequest.count(), seatCreateRequest.seatType());

        return ownerReservationRepository.saveSeat(seat);
    }

    @Transactional
    public Long createShopReservationDateTime(Long shopId, ShopReservationDateTimeCreateRequest shopReservationDateTimeCreateRequest) {
        var shopReservation = ownerReservationRepository.findShopReservationByShopId(shopId).orElseThrow(() -> new NoSuchElementException("해당 ID의 매장의 예약정보가 존재하지 않습니다:" + shopId));
        var shopReservationDateTime = new ShopReservationDateTime(shopReservation, shopReservationDateTimeCreateRequest.localDate(), shopReservationDateTimeCreateRequest.localTime());

        var shopReservationDateTimeId = ownerReservationRepository.saveShopReservationDateTime(shopReservationDateTime);

        var seats = ownerReservationRepository.findAllSeatsByShopId(shopId);
        var shopReservationDateTimeSeats = seats.stream()
                .map(seat -> new ShopReservationDateTimeSeat(shopReservationDateTime, seat)).toList();
        ownerReservationRepository.saveAllShopReservationDateTimeSeat(shopReservationDateTimeSeats);

        return shopReservationDateTimeId;
    }

    @Transactional
    public void cancelReservationByOwner(Long reservationId) {
        var reservation = ownerReservationRepository.findReservationById(reservationId).orElseThrow(() -> new NoSuchElementException("해당 ID의 예약 정보가 없습니다.: " + reservationId));
        reservation.updateReservationStatus(ReservationStatus.CANCEL);
    }

    @Transactional
    public void markReservationAsVisitedByOwner(Long reservationId) {
        var reservation = ownerReservationRepository.findReservationById(reservationId).orElseThrow(() -> new NoSuchElementException("해당 ID의 예약 정보가 없습니다.: " + reservationId));
        reservation.updateReservationStatus(ReservationStatus.VISITED);
    }

    @Transactional
    public void markReservationAsNoShowByOwner(Long reservationId) {
        var reservation = ownerReservationRepository.findReservationById(reservationId).orElseThrow(() -> new NoSuchElementException("해당 ID의 예약 정보가 없습니다.: " + reservationId));
        reservation.updateReservationStatus(ReservationStatus.NO_SHOW);
    }
}