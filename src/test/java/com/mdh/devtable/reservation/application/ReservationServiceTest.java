package com.mdh.devtable.reservation.application;

import com.mdh.devtable.DataInitializerFactory;
import com.mdh.devtable.reservation.controller.dto.ReservationCreateRequest;
import com.mdh.devtable.reservation.domain.Reservation;
import com.mdh.devtable.reservation.infra.persistence.ReservationRepository;
import com.mdh.devtable.reservation.infra.persistence.ShopReservationDateTimeSeatRepository;
import com.mdh.devtable.reservation.infra.persistence.ShopReservationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyIterable;
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
    private ShopReservationRepository shopReservationRepository;

    @Mock
    private ShopReservationDateTimeSeatRepository shopReservationDateTimeSeatRepository;

    @Test
    @DisplayName("예약을 등록한다.")
    void createReservationTest() {
        //given
        var shopReservation = DataInitializerFactory.shopReservation(1L, 2, 10);

        var seat = DataInitializerFactory.seat(shopReservation);

        var shopReservationDateTime = DataInitializerFactory.shopReservationDateTime(shopReservation);

        var shopReservationDateTimeSeat1 = DataInitializerFactory.shopReservationDateTimeSeat(shopReservationDateTime, seat);
        var shopReservationDateTimeSeat2 = DataInitializerFactory.shopReservationDateTimeSeat(shopReservationDateTime, seat);

        var reservation = DataInitializerFactory.reservation(1L, shopReservation, 3);

        given(shopReservationRepository.findById(any(Long.class)))
                .willReturn(Optional.ofNullable(shopReservation));
        given(shopReservationDateTimeSeatRepository.findAllById(anyIterable()))
                .willReturn(List.of(shopReservationDateTimeSeat1, shopReservationDateTimeSeat2));
        given(reservationRepository.save(any(Reservation.class)))
                .willReturn(reservation);

        var reservationCreateRequest = new ReservationCreateRequest(1L,
                2L,
                List.of(3L, 4L),
                "요구사항 입니다.",
                2);

        //when
        reservationService.createReservation(reservationCreateRequest);

        //then
        verify(shopReservationRepository, times(1)).findById(any(Long.class));
        verify(shopReservationDateTimeSeatRepository, times(1)).findAllById(anyIterable());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    @DisplayName("해당 매장의 예약 정보가 없다면 예외가 발생한다.")
    void createReservationNoSuchShopReservationExTest() {
        //given
        var shopId = 1L;

        given(shopReservationRepository.findById(any(Long.class)))
                .willReturn(Optional.empty());

        var reservationCreateRequest = new ReservationCreateRequest(1L,
                shopId,
                List.of(3L, 4L, 5L),
                "요구사항 입니다.",
                2);

        //when&then
        assertThatThrownBy(() -> reservationService.createReservation(reservationCreateRequest))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("매장의 예약 정보가 없습니다. shopId " + shopId);
    }

    @Test
    @DisplayName("예약 좌석들 중 일부가 없다면 예외가 발생한다.")
    void ecreateReservationTest() {
        //given
        var shopReservation = DataInitializerFactory.shopReservation(1L, 2, 10);
        var shopReservationDateTimeSeatId = 3L;

        var seat = DataInitializerFactory.seat(shopReservation);

        var shopReservationDateTime = DataInitializerFactory.shopReservationDateTime(shopReservation);

        var shopReservationDateTimeSeat = DataInitializerFactory.shopReservationDateTimeSeat(shopReservationDateTime, seat);


        given(shopReservationRepository.findById(any(Long.class)))
                .willReturn(Optional.ofNullable(shopReservation));
        given(shopReservationDateTimeSeatRepository.findAllById(anyIterable()))
                .willReturn(List.of(shopReservationDateTimeSeat));

        var reservationCreateRequest = new ReservationCreateRequest(1L,
                2L,
                List.of(shopReservationDateTimeSeatId, 4L),
                "요구사항 입니다.",
                2);

        //when&then
        assertThatThrownBy(() -> reservationService.createReservation(reservationCreateRequest))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("예약 좌석 정보들 중 일부가 없습니다.");
    }
}