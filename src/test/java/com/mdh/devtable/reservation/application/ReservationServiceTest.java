package com.mdh.devtable.reservation.application;

import com.mdh.devtable.DataInitializerFactory;
import com.mdh.devtable.reservation.domain.Reservation;
import com.mdh.devtable.reservation.domain.ReservationStatus;
import com.mdh.devtable.reservation.infra.persistence.ReservationRepository;
import com.mdh.devtable.reservation.infra.persistence.ShopReservationDateTimeSeatRepository;
import com.mdh.devtable.reservation.infra.persistence.ShopReservationRepository;
import com.mdh.devtable.reservation.presentation.dto.ReservationCancelRequest;
import com.mdh.devtable.reservation.presentation.dto.ReservationPreemptiveRequest;
import com.mdh.devtable.reservation.presentation.dto.ReservationRegisterRequest;
import com.mdh.devtable.reservation.presentation.dto.ReservationUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
    private Set<Long> preemtiveShopReservationDateTimeSeats;

    @Mock
    private Map<UUID, Reservation> preemtiveReservations;

    @Test
    @DisplayName("예약을 선점한다.")
    void preemptiveReservationTest() {
        //given
        var userId = 1L;
        var shopReservationDateTimeSeatIds = List.of(1L, 2L, 3L);
        var requirement = "요구사항입니다.";
        var personCount = 3;
        var reservationPreemptiveRequest = new ReservationPreemptiveRequest(userId,
                shopReservationDateTimeSeatIds,
                requirement,
                personCount);
        var reservation = DataInitializerFactory.preemptiveReservation(userId, personCount);

        given(preemtiveShopReservationDateTimeSeats.contains(any(Long.class))).willReturn(false);
        given(preemtiveShopReservationDateTimeSeats.addAll(anyList())).willReturn(true);
        given(preemtiveReservations.put(any(UUID.class), any(Reservation.class))).willReturn(reservation);

        //when
        UUID reservationId = reservationService.preemtiveReservation(reservationPreemptiveRequest);

        //then
        verify(preemtiveShopReservationDateTimeSeats, times(3)).contains(any(Long.class));
        verify(preemtiveShopReservationDateTimeSeats, times(1)).addAll(anyList());
        verify(preemtiveReservations, times(1)).put(any(UUID.class), any(Reservation.class));

        assertThat(reservationId).isEqualTo(reservation.getReservationId());
    }

    @Test
    @DisplayName("좌석을 선점할 때 이미 선점된 좌석이면 예외가 발생한다.")
    void preemptiveReservationExTest() {
        //given
        var userId = 1L;
        var shopReservationDateTimeSeatIds = List.of(1L, 2L, 3L);
        var requirement = "요구사항입니다.";
        var personCount = 3;
        var reservationPreemptiveRequest = new ReservationPreemptiveRequest(userId,
                shopReservationDateTimeSeatIds,
                requirement,
                personCount);
        var reservation = DataInitializerFactory.preemptiveReservation(userId, personCount);

        given(preemtiveShopReservationDateTimeSeats.contains(any(Long.class))).willReturn(true);

        //when
        assertThatThrownBy(() -> reservationService.preemtiveReservation(reservationPreemptiveRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 선점된 좌석이므로 선점할 수 없습니다.");
    }

    @Test
    @DisplayName("선점한 예약을 확정한다.")
    void registerReservationTest() {
        //given
        var shopReservation = DataInitializerFactory.shopReservation(1L, 2, 10);

        var seatCount = 2;
        var seat = DataInitializerFactory.seat(shopReservation, seatCount);

        var shopReservationDateTime = DataInitializerFactory.shopReservationDateTime(shopReservation);

        var shopReservationDateTimeSeat1 = DataInitializerFactory.shopReservationDateTimeSeat(shopReservationDateTime, seat);
        var shopReservationDateTimeSeat2 = DataInitializerFactory.shopReservationDateTimeSeat(shopReservationDateTime, seat);

        var reservationRegisterRequest = new ReservationRegisterRequest(1L,
                List.of(1L, 2L),
                4);
        var reservation = DataInitializerFactory.preemptiveReservation(1L, 3);
        var reservationId = reservation.getReservationId();

        given(preemtiveShopReservationDateTimeSeats.contains(any(Long.class))).willReturn(true);
        given(preemtiveReservations.containsKey(any(UUID.class))).willReturn(true);
        given(preemtiveReservations.get(any(UUID.class))).willReturn(reservation);

        given(shopReservationRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(shopReservation));
        given(shopReservationDateTimeSeatRepository.findAllById(anyIterable())).willReturn(List.of(shopReservationDateTimeSeat1, shopReservationDateTimeSeat2));

        given(reservationRepository.save(any(Reservation.class))).willReturn(reservation);

        given(preemtiveReservations.remove(any(UUID.class))).willReturn(reservation);
        given(preemtiveShopReservationDateTimeSeats.remove(any(Long.class))).willReturn(true);

        //when
        reservationService.registerReservation(reservationId, reservationRegisterRequest);

        //then
        verify(preemtiveShopReservationDateTimeSeats, times(2)).contains(any(Long.class));
        verify(preemtiveReservations, times(1)).containsKey(any(UUID.class));
        verify(preemtiveReservations, times(1)).get(any(UUID.class));

        verify(shopReservationRepository, times(1)).findById(any(Long.class));
        verify(shopReservationDateTimeSeatRepository, times(1)).findAllById(anyIterable());

        verify(reservationRepository, times(1)).save(any(Reservation.class));

        verify(preemtiveReservations, times(1)).remove(any(UUID.class));
        verify(preemtiveShopReservationDateTimeSeats, times(2)).remove(any(Long.class));
    }

    @Test
    @DisplayName("선점된 예약이 아니라면 예약을 확정할 때 예외가 발생한다.")
    void registerReservationNotPreemptiveReservationExTest() {
        //given
        var reservationRegisterRequest = new ReservationRegisterRequest(1L,
                List.of(1L, 2L),
                4);
        var reservation = DataInitializerFactory.preemptiveReservation(1L, 3);
        var reservationId = reservation.getReservationId();

        given(preemtiveReservations.containsKey(any(UUID.class))).willReturn(false);

        //when & then
        assertThatThrownBy(() -> reservationService.registerReservation(reservationId, reservationRegisterRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("선점된 예약이 아니므로 예약 확정할 수 없습니다.");
    }

    @Test
    @DisplayName("선점된 좌석이 아니라면 예약을 확정할 때 예외가 발생한다.")
    void registerReservationNotPreemptiveSeatExTest() {
        //given
        var reservationRegisterRequest = new ReservationRegisterRequest(1L,
                List.of(1L, 2L),
                4);
        var reservation = DataInitializerFactory.preemptiveReservation(1L, 3);
        var reservationId = reservation.getReservationId();

        given(preemtiveReservations.containsKey(any(UUID.class))).willReturn(true);
        given(preemtiveShopReservationDateTimeSeats.contains(any(Long.class))).willReturn(false);

        //when & then
        assertThatThrownBy(() -> reservationService.registerReservation(reservationId, reservationRegisterRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("선점된 좌석이 아니므로 예약 확정할 수 없습니다.");
    }

    @Test
    @DisplayName("해당 매장의 예약 정보가 없다면 예약을 확정할 때 예외가 발생한다.")
    void registerReservationNotFoundShopReservationExTest() {
        //given
        var shopId = 1L;
        var reservationRegisterRequest = new ReservationRegisterRequest(shopId,
                List.of(1L, 2L),
                4);
        var reservation = DataInitializerFactory.preemptiveReservation(1L, 3);
        var reservationId = reservation.getReservationId();

        given(preemtiveShopReservationDateTimeSeats.contains(any(Long.class))).willReturn(true);
        given(preemtiveReservations.containsKey(any(UUID.class))).willReturn(true);
        given(preemtiveReservations.get(any(UUID.class))).willReturn(reservation);

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
        var reservation = DataInitializerFactory.preemptiveReservation(1L, 3);
        var reservationId = reservation.getReservationId();

        given(preemtiveShopReservationDateTimeSeats.contains(any(Long.class))).willReturn(true);
        given(preemtiveReservations.containsKey(any(UUID.class))).willReturn(true);
        given(preemtiveReservations.get(any(UUID.class))).willReturn(reservation);

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
        var reservation = DataInitializerFactory.preemptiveReservation(1L, 3);
        var reservationId = reservation.getReservationId();

        given(preemtiveShopReservationDateTimeSeats.contains(any(Long.class))).willReturn(true);
        given(preemtiveReservations.containsKey(any(UUID.class))).willReturn(true);

        given(preemtiveReservations.remove(any(UUID.class))).willReturn(reservation);
        given(preemtiveShopReservationDateTimeSeats.remove(any(Long.class))).willReturn(true);

        //when
        String message = reservationService.cancelPreemptiveReservation(reservationId, reservationCancelRequest);

        //then
        verify(preemtiveShopReservationDateTimeSeats, times(2)).contains(any(Long.class));
        verify(preemtiveReservations, times(1)).containsKey(any(UUID.class));

        verify(preemtiveReservations, times(1)).remove(any(UUID.class));
        verify(preemtiveShopReservationDateTimeSeats, times(2)).remove(any(Long.class));

        assertThat(message).isEqualTo("성공적으로 선점된 예약을 취소했습니다.");
    }

    @Test
    @DisplayName("선점된 예약이 아니라면 예약을 취소할 때 예외가 발생한다.")
    void registerCancelNotPreemptiveReservationTest() {
        //given
        var reservationCancelRequest = new ReservationCancelRequest(List.of(1L, 2L));
        var reservation = DataInitializerFactory.preemptiveReservation(1L, 3);
        var reservationId = reservation.getReservationId();

        given(preemtiveReservations.containsKey(any(UUID.class))).willReturn(false);

        //when & then
        assertThatThrownBy(() -> reservationService.cancelPreemptiveReservation(reservationId, reservationCancelRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("선점된 예약이 아니므로 예약 취소할 수 없습니다.");
    }

    @Test
    @DisplayName("선점된 좌석이 아니라면 예약을 취소할 때 예외가 발생한다.")
    void registerCancelNotPreemptiveSeatTest() {
        //given
        var reservationCancelRequest = new ReservationCancelRequest(List.of(1L, 2L));
        var reservation = DataInitializerFactory.preemptiveReservation(1L, 3);
        var reservationId = reservation.getReservationId();

        given(preemtiveReservations.containsKey(any(UUID.class))).willReturn(true);
        given(preemtiveShopReservationDateTimeSeats.contains(any(Long.class))).willReturn(false);

        //when & then
        assertThatThrownBy(() -> reservationService.cancelPreemptiveReservation(reservationId, reservationCancelRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("선점된 좌석이 아니므로 예약 취소할 수 없습니다.");
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
        var shopReservationDateTime = DataInitializerFactory.shopReservationDateTime(shopReservation, LocalDate.now().plusDays(1), LocalTime.now().plusHours(1));

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