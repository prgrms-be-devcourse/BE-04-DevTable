package com.mdh.devtable.reservation.domain;

import com.mdh.devtable.DataInitializerFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ShopReservationDateTimeSeatTest {

    @Test
    @DisplayName("좌석을 예약한다.")
    void registerReservationTest() {
        //given
        var shopReservation = DataInitializerFactory.shopReservation(1L, 2, 10);

        var seat = new Seat(shopReservation, SeatType.ROOM);

        var shopReservationDateTime = new ShopReservationDateTime(shopReservation, LocalDate.now(), LocalTime.now());
        var shopReservationDateTimeSeat = new ShopReservationDateTimeSeat(shopReservationDateTime, seat);

        var reservation = DataInitializerFactory.reservation(1L, shopReservation, 4);

        //when
        shopReservationDateTimeSeat.registerReservation(reservation);

        //then
        assertThat(shopReservationDateTimeSeat.getReservation()).isEqualTo(reservation);
        assertThat(shopReservationDateTimeSeat.getSeatStatus()).isEqualTo(SeatStatus.UNAVAILABLE);
    }

    @Test
    @DisplayName("이미 예약된 좌석을 예약할 수 없다.")
    void registerReservationExTest() {
        //given
        var shopReservation = DataInitializerFactory.shopReservation(1L, 2, 10);

        var seat = new Seat(shopReservation, SeatType.ROOM);

        var shopReservationDateTime = new ShopReservationDateTime(shopReservation, LocalDate.now(), LocalTime.now());
        var shopReservationDateTimeSeat = new ShopReservationDateTimeSeat(shopReservationDateTime, seat);

        var reservation = DataInitializerFactory.reservation(1L, shopReservation, 4);

        shopReservationDateTimeSeat.registerReservation(reservation);

        //when & then
        assertThatThrownBy(() -> shopReservationDateTimeSeat.registerReservation(reservation))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("예약된 좌석은 다시 예약할 수 없습니다.");
    }

    @Test
    @DisplayName("예약된 좌석을 취소할 수 있다.")
    void cancleReservationTest() {
        //given
        var shopReservation = DataInitializerFactory.shopReservation(1L, 2, 10);

        var seat = new Seat(shopReservation, SeatType.ROOM);

        var shopReservationDateTime = new ShopReservationDateTime(shopReservation, LocalDate.now(), LocalTime.now());
        var shopReservationDateTimeSeat = new ShopReservationDateTimeSeat(shopReservationDateTime, seat);

        var reservation = DataInitializerFactory.reservation(1L, shopReservation, 4);

        shopReservationDateTimeSeat.registerReservation(reservation);

        //when
        shopReservationDateTimeSeat.cancleReservation();

        //then
        assertThat(shopReservationDateTimeSeat.getReservation()).isNull();
        assertThat(shopReservationDateTimeSeat.getSeatStatus()).isEqualTo(SeatStatus.AVAILABLE);
    }

    @Test
    @DisplayName("예약 가능한 좌석은 취소할 수 없다.")
    void cancleReservationExTest() {
        //given
        var shopReservation = DataInitializerFactory.shopReservation(1L, 2, 10);

        var seat = new Seat(shopReservation, SeatType.ROOM);

        var shopReservationDateTime = new ShopReservationDateTime(shopReservation, LocalDate.now(), LocalTime.now());
        var shopReservationDateTimeSeat = new ShopReservationDateTimeSeat(shopReservationDateTime, seat);
        
        //when & then
        assertThatThrownBy(() -> shopReservationDateTimeSeat.cancleReservation())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("예약 가능한 좌석이므로 취소할 수 없습니다.");
    }
}