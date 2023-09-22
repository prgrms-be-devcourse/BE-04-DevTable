package com.mdh.user.reservation.application;

import com.mdh.common.reservation.domain.Reservation;
import com.mdh.common.reservation.domain.ReservationStatus;
import com.mdh.common.reservation.domain.ShopReservation;
import com.mdh.common.reservation.domain.ShopReservationDateTimeSeat;
import com.mdh.common.reservation.domain.event.ReservationCanceledEvent;
import com.mdh.common.reservation.domain.event.ReservationCreatedEvent;
import com.mdh.common.reservation.persistence.ReservationRepository;
import com.mdh.common.reservation.persistence.ShopReservationDateTimeSeatRepository;
import com.mdh.common.reservation.persistence.ShopReservationRepository;
import com.mdh.user.reservation.application.dto.ReservationResponse;
import com.mdh.user.reservation.application.dto.ReservationResponses;
import com.mdh.user.reservation.infra.persistence.ReservationCache;
import com.mdh.user.reservation.presentation.dto.ReservationCancelRequest;
import com.mdh.user.reservation.presentation.dto.ReservationPreemptiveRequest;
import com.mdh.user.reservation.presentation.dto.ReservationRegisterRequest;
import com.mdh.user.reservation.presentation.dto.ReservationUpdateRequest;
import io.micrometer.core.annotation.Counted;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ShopReservationDateTimeSeatRepository shopReservationDateTimeSeatRepository;
    private final ShopReservationRepository shopReservationRepository;

    private final ReservationCache reservationCache;

    private final ApplicationEventPublisher eventPublisher;

    public UUID preemtiveReservation(Long userId, ReservationPreemptiveRequest reservationPreemptiveRequest) {
        // 예약을 만듦
        var reservationId = UUID.randomUUID();
        var reservation = createReservation(reservationId, userId, reservationPreemptiveRequest);

        // 선점한 좌석 및 만든 좌석 캐시에 저장
        var shopReservationDateTimeSeatIds = reservationPreemptiveRequest.shopReservationDateTimeSeatIds();
        return reservationCache.preemp(shopReservationDateTimeSeatIds, reservation.getReservationId(), reservation);
    }

    private Reservation createReservation(UUID reservationId,
                                          Long userId,
                                          ReservationPreemptiveRequest reservationPreemptiveRequest) {
        return new Reservation(reservationId,
            userId,
            reservationPreemptiveRequest.requirement(),
            reservationPreemptiveRequest.personCount());
    }

    @Counted("user.reservation.register")
    @Transactional
    public Long registerReservation(UUID reservationId, ReservationRegisterRequest reservationRegisterRequest) {
        // 선점된 좌석인지 확인하면서 미리 만들어둔 예약 가져옴
        var shopReservationDateTimeSeatIds = reservationRegisterRequest.shopReservationDateTimeSeatIds();
        Reservation reservation = reservationCache.register(shopReservationDateTimeSeatIds, reservationId);

        // 예약 검증
        reservation.validSeatSizeAndPersonCount(reservationRegisterRequest.totalSeatCount());

        // shop reservation과 shop reservation datetime seat 영속 상태로 만듦
        var shopId = reservationRegisterRequest.shopId();
        var shopReservation = findShopReservation(shopId);
        var shopReservationDateTimeSeats = findShopReservationDateTimeSeats(shopReservationDateTimeSeatIds);

        // 연결함
        reservation.addShopReservation(shopReservation);
        reservation.addShopReservationDateTimeSeats(shopReservationDateTimeSeats);

        // reservation 저장
        var savedReservation = reservationRepository.save(reservation);

        // 캐시에서 삭제
        reservationCache.removeAll(shopReservationDateTimeSeatIds, reservationId);

        eventPublisher.publishEvent(new ReservationCreatedEvent(savedReservation));
        return savedReservation.getId();
    }

    private List<ShopReservationDateTimeSeat> findShopReservationDateTimeSeats(List<Long> shopReservationDateTimeSeatIds) {
        var shopReservationDateTimeSeats = shopReservationDateTimeSeatRepository.findAllById(shopReservationDateTimeSeatIds);
        if (shopReservationDateTimeSeats.size() < shopReservationDateTimeSeatIds.size()) {
            throw new NoSuchElementException("예약 좌석 정보들 중 일부가 없습니다.");
        }
        return shopReservationDateTimeSeats;
    }

    public String cancelPreemptiveReservation(UUID reservationId, ReservationCancelRequest reservationCancelRequest) {
        var shopReservationDateTimeSeatIds = reservationCancelRequest.shopReservationDateTimeSeatIds();
        reservationCache.removeAll(shopReservationDateTimeSeatIds, reservationId);
        return "성공적으로 선점된 예약을 취소했습니다.";
    }

    @Counted("user.reservation.cancel")
    @Transactional
    public String cancelReservation(Long reservationId) {
        var reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new NoSuchElementException("등록된 예약이 존재하지 않습니다. id : " + reservationId));

        if (reservation.isCancelShopReservation()) {
            eventPublisher.publishEvent(new ReservationCanceledEvent(reservation));
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

    private ShopReservation findShopReservation(Long shopId) {
        return shopReservationRepository.findById(shopId)
            .orElseThrow(() -> new NoSuchElementException("매장의 예약 정보가 없습니다. shopId " + shopId));
    }

    public ReservationResponses findAllReservations(Long userId, ReservationStatus reservationStatus) {
        var reservationResponses = reservationRepository.findByUserIdAndReservationStatus(userId, reservationStatus)
            .stream()
            .map(ReservationResponse::new)
            .toList();

        return new ReservationResponses(reservationResponses);
    }
}