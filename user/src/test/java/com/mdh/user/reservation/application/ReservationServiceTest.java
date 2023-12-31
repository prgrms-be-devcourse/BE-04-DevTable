package com.mdh.user.reservation.application;

import com.mdh.common.reservation.domain.Reservation;
import com.mdh.common.reservation.domain.ReservationStatus;
import com.mdh.common.reservation.domain.event.ReservationCanceledEvent;
import com.mdh.common.reservation.domain.event.ReservationCreatedEvent;
import com.mdh.common.reservation.persistence.ReservationRepository;
import com.mdh.common.reservation.persistence.ShopReservationDateTimeSeatRepository;
import com.mdh.common.reservation.persistence.ShopReservationRepository;
import com.mdh.user.DataInitializerFactory;
import com.mdh.user.reservation.infra.persistence.ReservationCache;
import com.mdh.user.reservation.presentation.dto.ReservationCancelRequest;
import com.mdh.user.reservation.presentation.dto.ReservationPreemptiveRequest;
import com.mdh.user.reservation.presentation.dto.ReservationRegisterRequest;
import com.mdh.user.reservation.presentation.dto.ReservationUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ShopReservationDateTimeSeatRepository shopReservationDateTimeSeatRepository;

    @Mock
    private ShopReservationRepository shopReservationRepository;

    @Mock
    private ReservationCache reservationCache;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Test
    @DisplayName("예약을 선점한다.")
    void preemptiveReservationTest() {
        //given
        var userId = 1L;
        var shopReservationDateTimeSeatIds = List.of(1L, 2L, 3L);
        var requirement = "요구사항입니다.";
        var personCount = 3;
        var reservationPreemptiveRequest = new ReservationPreemptiveRequest(
            shopReservationDateTimeSeatIds,
            requirement,
            personCount);
        var reservationId = UUID.randomUUID();

        given(reservationCache.preemp(anyList(), any(UUID.class), any(Reservation.class))).willReturn(reservationId);

        //when
        var cachedReservationId = reservationService.preemtiveReservation(userId, reservationPreemptiveRequest);

        //then
        verify(reservationCache, times(1)).preemp(anyList(), any(UUID.class), any(Reservation.class));

        assertThat(cachedReservationId).isEqualTo(reservationId);
    }

    @Test
    @DisplayName("선점한 예약을 확정한다.")
    void registerReservationTest() {
        //given
        var shopId = 1L;
        var shopReservation = DataInitializerFactory.shopReservation(shopId, 2, 10);
        var seat = DataInitializerFactory.seat(shopReservation);
        var shopReservationDateTime = DataInitializerFactory.shopReservationDateTime(shopReservation);
        var shopReservationDateTimeSeat1 = DataInitializerFactory.shopReservationDateTimeSeat(shopReservationDateTime, seat);
        var shopReservationDateTimeSeat2 = DataInitializerFactory.shopReservationDateTimeSeat(shopReservationDateTime, seat);

        var reservationRegisterRequest = new ReservationRegisterRequest(1L,
            List.of(1L, 2L),
            4);

        var reservationId = UUID.randomUUID();
        var reservation = DataInitializerFactory.preemptiveReservation(reservationId, 1L, 3);

        given(reservationCache.register(anyList(), any(UUID.class))).willReturn(reservation);

        given(shopReservationRepository.findById(anyLong())).willReturn(Optional.ofNullable(shopReservation));
        given(shopReservationDateTimeSeatRepository.findAllById(anyList())).willReturn(List.of(shopReservationDateTimeSeat1, shopReservationDateTimeSeat2));

        given(reservationRepository.save(any(Reservation.class))).willReturn(reservation);

        doNothing().when(eventPublisher).publishEvent(any(ReservationCreatedEvent.class));

        //when
        reservationService.registerReservation(reservationId, reservationRegisterRequest);

        //then
        verify(reservationCache, times(1)).register(anyList(), any(UUID.class));

        verify(shopReservationRepository, times(1)).findById(anyLong());
        verify(shopReservationDateTimeSeatRepository, times(1)).findAllById(anyList());

        verify(reservationRepository, times(1)).save(any(Reservation.class));

        verify(reservationCache, times(1)).removeAll(anyList(), any(UUID.class));
    }

    @Test
    @DisplayName("해당 매장의 예약 정보가 없다면 예약을 확정할 때 예외가 발생한다.")
    void registerReservationNotFoundShopReservationExTest() {
        //given
        var shopId = 1L;
        var reservationRegisterRequest = new ReservationRegisterRequest(shopId,
            List.of(1L, 2L),
            4);
        var reservationId = UUID.randomUUID();
        var reservation = DataInitializerFactory.preemptiveReservation(reservationId, 1L, 3);

        given(reservationCache.register(anyList(), any(UUID.class))).willReturn(reservation);

        given(shopReservationRepository.findById(any(Long.class))).willReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> reservationService.registerReservation(reservationId, reservationRegisterRequest))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("매장의 예약 정보가 없습니다. shopId " + shopId);
    }

    @Test
    @DisplayName("해당 예약 좌석 정보가 없다면 예약을 확정할 때 예외가 발생한다.")
    void registerReservationNotFoundSeatsExTest() {
        //given
        var shopReservation = DataInitializerFactory.shopReservation(1L, 2, 10);

        var seatCount = 2;
        var seat = DataInitializerFactory.seat(shopReservation, seatCount);

        var shopReservationDateTime = DataInitializerFactory.shopReservationDateTime(shopReservation);

        var shopReservationDateTimeSeat = DataInitializerFactory.shopReservationDateTimeSeat(shopReservationDateTime, seat);

        var reservationRegisterRequest = new ReservationRegisterRequest(1L,
            List.of(1L, 2L),
            4);
        var reservationId = UUID.randomUUID();
        var reservation = DataInitializerFactory.preemptiveReservation(reservationId, 1L, 3);

        given(reservationCache.register(anyList(), any(UUID.class))).willReturn(reservation);

        given(shopReservationRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(shopReservation));
        given(shopReservationDateTimeSeatRepository.findAllById(anyIterable())).willReturn(List.of(shopReservationDateTimeSeat));

        //when & then
        assertThatThrownBy(() -> reservationService.registerReservation(reservationId, reservationRegisterRequest))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("예약 좌석 정보들 중 일부가 없습니다.");
    }

    @Test
    @DisplayName("선점한 예약을 취소한다.")
    void registerCancelTest() {
        //given
        var reservationCancelRequest = new ReservationCancelRequest(List.of(1L, 2L));
        var reservationId = UUID.randomUUID();
        var reservation = DataInitializerFactory.preemptiveReservation(reservationId, 1L, 3);

        //when
        String message = reservationService.cancelPreemptiveReservation(reservationId, reservationCancelRequest);

        //then
        verify(reservationCache, times(1)).removeAll(anyList(), any(UUID.class));

        assertThat(message).isEqualTo("성공적으로 선점된 예약을 취소했습니다.");
    }

    @Test
    @DisplayName("예약한 좌석을 하루 전에 취소하면 정상적으로 취소된다.")
    void cancelReservationTest() {
        //given
        var userId = 1L;
        var shopId = 1L;
        var reservationId = 1L;
        var seatCount = 2;

        var shopReservation = DataInitializerFactory.shopReservation(shopId, 2, 8);
        var seat = DataInitializerFactory.seat(shopReservation, seatCount);
        var shopReservationDateTime = DataInitializerFactory.shopReservationDateTime(shopReservation, LocalDate.now().plusDays(2), LocalTime.now());

        var shopReservationDateTimeSeat1 = DataInitializerFactory.shopReservationDateTimeSeat(shopReservationDateTime, seat);
        var shopReservationDateTimeSeat2 = DataInitializerFactory.shopReservationDateTimeSeat(shopReservationDateTime, seat);
        var shopReservationDateTimeSeat3 = DataInitializerFactory.shopReservationDateTimeSeat(shopReservationDateTime, seat);

        var shopReservationDateTimeSeats = List.of(shopReservationDateTimeSeat1, shopReservationDateTimeSeat2, shopReservationDateTimeSeat3);

        var reservation = DataInitializerFactory.reservation(userId, shopReservation, 3);

        reservation.addShopReservationDateTimeSeats(shopReservationDateTimeSeats);

        given(reservationRepository.findById(any(Long.class))).willReturn(Optional.of(reservation));
        doNothing().when(eventPublisher).publishEvent(any(ReservationCanceledEvent.class));
        //when
        var result = reservationService.cancelReservation(reservationId);

        //then
        verify(reservationRepository, times(1)).findById(any(Long.class));

        assertThat(reservation.getShopReservationDateTimeSeats()).isEmpty();
        assertThat(reservation.getReservationStatus()).isEqualTo(ReservationStatus.CANCEL);
        assertThat(result).isEqualTo("정상적으로 예약이 취소되었습니다.");
    }

    @Test
    @DisplayName("예약한 좌석을 당일 날 취소하면 정상적으로 취소되지만 패널티가 발생 할 수 있다.")
    void cancelTodayReservationTest() {
        //given
        var userId = 1L;
        var shopId = 1L;
        var reservationId = 1L;
        var seatCount = 2;

        var shopReservation = DataInitializerFactory.shopReservation(shopId, 2, 8);
        var seat = DataInitializerFactory.seat(shopReservation, seatCount);
        var shopReservationDateTime = DataInitializerFactory.shopReservationDateTime(shopReservation);

        var shopReservationDateTimeSeat1 = DataInitializerFactory.shopReservationDateTimeSeat(shopReservationDateTime, seat);
        var shopReservationDateTimeSeat2 = DataInitializerFactory.shopReservationDateTimeSeat(shopReservationDateTime, seat);
        var shopReservationDateTimeSeat3 = DataInitializerFactory.shopReservationDateTimeSeat(shopReservationDateTime, seat);

        var shopReservationDateTimeSeats = List.of(shopReservationDateTimeSeat1, shopReservationDateTimeSeat2, shopReservationDateTimeSeat3);

        var reservation = DataInitializerFactory.reservation(userId, shopReservation, 3);
        reservation.addShopReservationDateTimeSeats(shopReservationDateTimeSeats);

        given(reservationRepository.findById(any(Long.class))).willReturn(Optional.of(reservation));

        //when
        var result = reservationService.cancelReservation(reservationId);

        //then
        verify(reservationRepository, times(1)).findById(any(Long.class));

        assertThat(reservation.getShopReservationDateTimeSeats()).isEmpty();
        assertThat(reservation.getReservationStatus()).isEqualTo(ReservationStatus.CANCEL);
        assertThat(result).isEqualTo("당일 취소의 경우 패널티가 발생 할 수 있습니다.");
    }

    @ParameterizedTest
    @EnumSource(value = ReservationStatus.class, mode = EnumSource.Mode.EXCLUDE, names = {"CREATED"})
    @DisplayName("생성 된 예약이 아니라면 예약을 취소 할 수 없습니다.")
    void cancelTodayReservationExTest(ReservationStatus reservationStatus) {
        //given
        var userId = 1L;
        var shopId = 1L;
        var reservationId = 1L;
        var seatCount = 2;

        var shopReservation = DataInitializerFactory.shopReservation(shopId, 2, 8);
        var seat = DataInitializerFactory.seat(shopReservation, seatCount);
        var shopReservationDateTime = DataInitializerFactory.shopReservationDateTime(shopReservation);

        var shopReservationDateTimeSeat1 = DataInitializerFactory.shopReservationDateTimeSeat(shopReservationDateTime, seat);
        var shopReservationDateTimeSeat2 = DataInitializerFactory.shopReservationDateTimeSeat(shopReservationDateTime, seat);
        var shopReservationDateTimeSeat3 = DataInitializerFactory.shopReservationDateTimeSeat(shopReservationDateTime, seat);

        var shopReservationDateTimeSeats = List.of(shopReservationDateTimeSeat1, shopReservationDateTimeSeat2, shopReservationDateTimeSeat3);

        var reservation = DataInitializerFactory.reservation(userId, shopReservation, 3);

        reservation.addShopReservationDateTimeSeats(shopReservationDateTimeSeats);

        given(reservationRepository.findById(any(Long.class))).willReturn(Optional.of(reservation));
        reservation.updateReservationStatus(reservationStatus);

        //when & then
        assertThatThrownBy(() -> reservationService.cancelReservation(reservationId)).isInstanceOf(IllegalStateException.class).hasMessage("생성 상태가 아니라면 예약을 취소 할 수 없습니다.");
    }

    @Test
    @DisplayName("매장 예약을 24시간 이전에는 수정 할 수 있다.")
    void updateReservationTest() {
        //given
        var userId = 1L;
        var shopId = 1L;
        var reservationId = 1L;
        var seatCount = 2;

        var shopReservation = DataInitializerFactory.shopReservation(shopId, 2, 8);
        var seat = DataInitializerFactory.seat(shopReservation, seatCount);
        var shopReservationDateTime = DataInitializerFactory.shopReservationDateTime(shopReservation, LocalDate.now().plusDays(2), LocalTime.now().plusHours(1));

        var shopReservationDateTimeSeat1 = DataInitializerFactory.shopReservationDateTimeSeat(shopReservationDateTime, seat);
        var shopReservationDateTimeSeat2 = DataInitializerFactory.shopReservationDateTimeSeat(shopReservationDateTime, seat);
        var shopReservationDateTimeSeat3 = DataInitializerFactory.shopReservationDateTimeSeat(shopReservationDateTime, seat);

        var shopReservationDateTimeSeats = List.of(shopReservationDateTimeSeat1, shopReservationDateTimeSeat2, shopReservationDateTimeSeat3);

        var reservation = DataInitializerFactory.reservation(userId, shopReservation, 3);
        var request = new ReservationUpdateRequest(List.of(4L, 5L, 6L, 7L), 4);

        reservation.addShopReservationDateTimeSeats(shopReservationDateTimeSeats);

        given(reservationRepository.findById(any(Long.class))).willReturn(Optional.of(reservation));
        given(shopReservationDateTimeSeatRepository.findAllById(anyList())).willReturn(List.of(shopReservationDateTimeSeat1, shopReservationDateTimeSeat2, shopReservationDateTimeSeat3));

        //when
        reservationService.updateReservation(reservationId, request);

        //then
        verify(reservationRepository, times(1)).findById(any(Long.class));
        verify(shopReservationDateTimeSeatRepository, times(1)).findAllById(anyList());
    }

    @Test
    @DisplayName("매장 예약이 24시간 이내라면 수정이 불가능하다.")
    void updateReservationExTest() {
        //given
        var userId = 1L;
        var shopId = 1L;
        var reservationId = 1L;
        var seatCount = 2;

        var shopReservation = DataInitializerFactory.shopReservation(shopId, 2, 8);
        var seat = DataInitializerFactory.seat(shopReservation, seatCount);
        var shopReservationDateTime = DataInitializerFactory.shopReservationDateTime(shopReservation);

        var shopReservationDateTimeSeat1 = DataInitializerFactory.shopReservationDateTimeSeat(shopReservationDateTime, seat);
        var shopReservationDateTimeSeat2 = DataInitializerFactory.shopReservationDateTimeSeat(shopReservationDateTime, seat);
        var shopReservationDateTimeSeat3 = DataInitializerFactory.shopReservationDateTimeSeat(shopReservationDateTime, seat);

        var shopReservationDateTimeSeats = List.of(shopReservationDateTimeSeat1, shopReservationDateTimeSeat2, shopReservationDateTimeSeat3);

        var reservation = DataInitializerFactory.reservation(userId, shopReservation, 3);
        var request = new ReservationUpdateRequest(List.of(4L, 5L, 6L, 7L), 4);

        reservation.addShopReservationDateTimeSeats(shopReservationDateTimeSeats);

        given(reservationRepository.findById(any(Long.class))).willReturn(Optional.of(reservation));
        given(shopReservationDateTimeSeatRepository.findAllById(anyList())).willReturn(List.of(shopReservationDateTimeSeat1, shopReservationDateTimeSeat2, shopReservationDateTimeSeat3));

        //when && then
        assertThatThrownBy(() -> reservationService.updateReservation(reservationId, request))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("예약이 24시간 이내로 남은 경우 예약 수정이 불가능합니다.");

        verify(reservationRepository, times(1)).findById(any(Long.class));
        verify(shopReservationDateTimeSeatRepository, times(1)).findAllById(anyList());
    }

}