package com.mdh.devtable.reservation;

import com.mdh.devtable.reservation.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ShopReservationDateTimeSeatTest {

    @Test
    @DisplayName("예약 좌석 상태를 변경할 수 있다.")
    void changeSeatStatusTest() {
        //given
        var seat = new Seat(SeatType.ROOM);
        var shopReservationDateTime = new ShopReservationDateTime(LocalDate.now(), LocalTime.now());
        var shopReservationDateTimeSeat = new ShopReservationDateTimeSeat(shopReservationDateTime, seat);

        //when
        shopReservationDateTimeSeat.changeSeatStaus(SeatStatus.UNAVAILABLE);

        //then
        assertThat(shopReservationDateTimeSeat.getSeatStatus()).isEqualTo(SeatStatus.UNAVAILABLE);
    }

    @Test
    @DisplayName("동일한 예약 좌석 상태로 변경할 수 없다.")
    void changeSeatStatusExTest() {
        //given
        var seat = new Seat(SeatType.ROOM);
        var shopReservationDateTime = new ShopReservationDateTime(LocalDate.now(), LocalTime.now());
        var shopReservationDateTimeSeat = new ShopReservationDateTimeSeat(shopReservationDateTime, seat);

        //when&then
        assertThatThrownBy(() -> shopReservationDateTimeSeat.changeSeatStaus(SeatStatus.AVAILABLE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("같은 좌석 상태로 변경할 수 없습니다.");
    }
}