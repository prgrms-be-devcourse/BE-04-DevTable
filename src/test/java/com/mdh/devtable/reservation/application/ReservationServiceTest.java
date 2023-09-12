package com.mdh.devtable.reservation.application;

import com.mdh.devtable.DataInitializerFactory;
import com.mdh.devtable.reservation.controller.dto.ReservationCreateRequest;
import com.mdh.devtable.reservation.domain.Reservation;
import com.mdh.devtable.reservation.infra.persistence.ReservationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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
    private ReservationValidator reservationValidator;

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

        given(reservationValidator.validShopReservation(any(Long.class))).willReturn(shopReservation);
        given(reservationValidator.validShopReservationDateTimeSeats(anyList()))
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
        verify(reservationValidator, times(1)).validShopReservation(any(Long.class));
        verify(reservationValidator, times(1)).validShopReservationDateTimeSeats(anyList());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    @DisplayName("예약하려는 좌석들의 수가 예약 인원 수를 넘어가면 예외가 발생한다.")
    void createReservationeTest() {
        //given
        var shopReservation = DataInitializerFactory.shopReservation(1L, 2, 10);

        var seat = DataInitializerFactory.seat(shopReservation);

        var shopReservationDateTime = DataInitializerFactory.shopReservationDateTime(shopReservation);

        var shopReservationDateTimeSeat1 = DataInitializerFactory.shopReservationDateTimeSeat(shopReservationDateTime, seat);
        var shopReservationDateTimeSeat2 = DataInitializerFactory.shopReservationDateTimeSeat(shopReservationDateTime, seat);
        var shopReservationDateTimeSeat3 = DataInitializerFactory.shopReservationDateTimeSeat(shopReservationDateTime, seat);

        var reservation = DataInitializerFactory.reservation(1L, shopReservation, 2);

        given(reservationValidator.validShopReservation(any(Long.class))).willReturn(shopReservation);
        given(reservationValidator.validShopReservationDateTimeSeats(anyList()))
                .willReturn(List.of(shopReservationDateTimeSeat1, shopReservationDateTimeSeat2, shopReservationDateTimeSeat3));
        given(reservationRepository.save(any(Reservation.class)))
                .willReturn(reservation);

        var shopReservationDateTimeSeats = List.of(3L, 4L, 5L);
        var personCount = 2;

        var reservationCreateRequest = new ReservationCreateRequest(1L,
                2L,
                shopReservationDateTimeSeats,
                "요구사항 입니다.",
                personCount);

        //when&then
        assertThatThrownBy(() -> reservationService.createReservation(reservationCreateRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("예약하려는 좌석의 수가 예약 인원 수를 초과했습니다. seats size : " + shopReservationDateTimeSeats.size() + ", person count : " + personCount);
    }
}