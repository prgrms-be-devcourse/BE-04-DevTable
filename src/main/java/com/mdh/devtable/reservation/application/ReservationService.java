package com.mdh.devtable.reservation.application;

import com.mdh.devtable.reservation.controller.dto.ReservationCancelRequest;
import com.mdh.devtable.reservation.controller.dto.ReservationPreemptiveRequest;
import com.mdh.devtable.reservation.controller.dto.ReservationRegisterRequest;
import com.mdh.devtable.reservation.domain.Reservation;
import com.mdh.devtable.reservation.domain.ShopReservation;
import com.mdh.devtable.reservation.domain.ShopReservationDateTimeSeat;
import com.mdh.devtable.reservation.infra.persistence.ReservationRepository;
import com.mdh.devtable.reservation.infra.persistence.ShopReservationDateTimeSeatRepository;
import com.mdh.devtable.reservation.infra.persistence.ShopReservationRepository;
import com.mdh.devtable.reservation.presentation.dto.ReservationUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ShopReservationDateTimeSeatRepository shopReservationDateTimeSeatRepository;
    private final ShopReservationRepository shopReservationRepository;

    private final Set<Long> preemtiveShopReservationDateTimeSeats;

    private final Map<UUID, Reservation> preemtiveReservations;

    public UUID preemtiveReservation(ReservationPreemptiveRequest reservationPreemtiveRequest) {
        // 선점된 좌석인지 확인한다.
        var shopReservationDateTimeSeatIds = reservationPreemtiveRequest.shopReservationDateTimeSeatIds();
        validPreemtiveShopReservationDateTimeSeats(shopReservationDateTimeSeatIds);

        // 모든 좌석을 선점함
        preemtiveShopReservationDateTimeSeats.addAll(reservationPreemtiveRequest.shopReservationDateTimeSeatIds());

        // 예약을 만듦
        var reservation = createReservation(reservationPreemtiveRequest);

        // 만든 예약을 캐시에 저장
        var createdReservation = preemtiveReservations.put(reservation.getReservationId(), reservation);

        return createdReservation.getReservationId();
    }

    @Transactional
    public void registerReservation(UUID reservationId, ReservationRegisterRequest reservationRegisterRequest) {
        // 선점한 예약인지 확인
        var shopReservationDateTimeSeatIds = reservationRegisterRequest.shopReservationDateTimeSeatIds();
        validRegisterReservations(reservationId, shopReservationDateTimeSeatIds);

        // 미리 만들었던 예약을 가져옴
        var reservation = preemtiveReservations.get(reservationId);

        // 예약 검증
        reservation.validSeatSizeAndPersonCount(reservationRegisterRequest.totalSeatCount());

        // shop reservation과 shop reservation datetime seat 영속 상태로 만듦
        var shopId = reservationRegisterRequest.shopId();
        var shopReservation = findShopReservation(shopId);
        var shopReservationDateTimeSeats = findShopReservationDateTimeSeats(shopReservationDateTimeSeatIds);

        // reservation 저장
        var savedReservation = reservationRepository.save(reservation);
        reservation.addShopReservationDateTimeSeats(shopReservationDateTimeSeats);

        // 연결함
        savedReservation.addShopReservation(shopReservation);
        shopReservationDateTimeSeats.forEach(savedReservation::addShopReservationDateTimeSeats);

        // 모두 삭제
        removeAll(reservationId, shopReservationDateTimeSeatIds);
    }

    public void cancelPreemptiveReservation(UUID reservationId, ReservationCancelRequest reservationCancelRequest) {
        var shopReservationDateTimeSeatIds = reservationCancelRequest.shopReservationDateTimeSeatIds();
        validCancelReservations(reservationId, shopReservationDateTimeSeatIds);

        // 선점에서 모두 삭제
        removeAll(reservationId, shopReservationDateTimeSeatIds);
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

    @Transactional
    public void updateReservation(Long reservationId, ReservationUpdateRequest request) {
        var reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NoSuchElementException("등록된 예약이 존재하지 않습니다. id : " + reservationId));

        var shopReservationDateTimeSeats =
                shopReservationDateTimeSeatRepository.findAllById(request.shopReservationDateTimeSeatsIds());

        reservation.updateReservation(shopReservationDateTimeSeats);
    }

    public Reservation createReservation(ReservationPreemptiveRequest reservationPreemptiveRequest) {
        return new Reservation(reservationPreemptiveRequest.userId(), reservationPreemptiveRequest.requirement(), reservationPreemptiveRequest.personCount());
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

    private void validPreemtiveShopReservationDateTimeSeats(List<Long> shopReservationDateTimeSeatIds) {
        // 선점된 좌석인지 확인
        shopReservationDateTimeSeatIds.forEach(shopReservationDateTimeSeatId -> {
            if (preemtiveShopReservationDateTimeSeats.contains(shopReservationDateTimeSeatId)) {
                throw new IllegalArgumentException("이미 선점된 좌석이므로 선점할 수 없습니다.");
            }
        });
    }

    private void validRegisterReservations(UUID reservationId, List<Long> shopReservationDateTimeSeatIds) {
        // 선점한 예약인지 확인
        if (!preemtiveReservations.containsKey(reservationId)) {
            throw new IllegalArgumentException("선점된 예약이 아니므로 예약 확정할 수 없습니다.");
        }
        // 선점된 좌석인지 확인
        shopReservationDateTimeSeatIds.forEach(shopReservationDateTimeSeatId -> {
            if (!preemtiveShopReservationDateTimeSeats.contains(shopReservationDateTimeSeatId)) {
                throw new IllegalArgumentException("선점된 좌석이 아니므로 예약 확정할 수 없습니다.");
            }
        });
    }

    private void validCancelReservations(UUID reservationId, List<Long> shopReservationDateTimeSeatIds) {
        // 선점한 예약인지 확인
        if (!preemtiveReservations.containsKey(reservationId)) {
            throw new IllegalArgumentException("선점된 예약이 아니므로 예약 취소할 수 없습니다.");
        }
        // 선점된 좌석인지 확인
        shopReservationDateTimeSeatIds.forEach(shopReservationDateTimeSeatId -> {
            if (!preemtiveShopReservationDateTimeSeats.contains(shopReservationDateTimeSeatId)) {
                throw new IllegalArgumentException("선점된 좌석이 아니므로 예약 취소할 수 없습니다.");
            }
        });
    }

    private void removeAll(UUID reservationId, List<Long> shopReservationDateTimeSeatIds) {
        preemtiveReservations.remove(reservationId);
        shopReservationDateTimeSeatIds.forEach(preemtiveShopReservationDateTimeSeats::remove);
    }
}