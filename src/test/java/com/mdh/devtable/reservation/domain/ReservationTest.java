package com.mdh.devtable.reservation.domain;

import com.mdh.devtable.DataInitializerFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReservationTest {

    @Test
    @DisplayName("예약을 생성 할 수 있다.")
    void createReservationTest() {
        //given
        var shopId = 1L;
        var minimumCount = 2;
        var maximumCount = 30;
        var shopReservation = DataInitializerFactory.shopReservation(shopId, minimumCount, maximumCount);

        var userId = 1L;
        var personCount = 3;

        //when
        var reservation = DataInitializerFactory.reservation(userId, shopReservation, personCount);

        //then
        assertThat(reservation.getReservationStatus()).isEqualTo(ReservationStatus.CREATED);
        assertThat(reservation.getPersonCount()).isEqualTo(3);
    }

    @ParameterizedTest
    @CsvSource(value = {"2 30 : 1", "1 10 : 11"}, delimiter = ':')
    @DisplayName("예약 생성 시 매장의 예약 정보에 있는 최소, 최대 인원수에 벗어나게 예약 할 수 없다.")
    void createReservationExTest(String shopWaitingInfo, int personCount) {
        //given
        var shopId = 1L;
        var shopWaitingInfos = shopWaitingInfo.split(" ");
        var minimumCount = Integer.parseInt(shopWaitingInfos[0]);
        var maximumCount = Integer.parseInt(shopWaitingInfos[1]);

        var shopReservation = DataInitializerFactory.shopReservation(shopId, minimumCount, maximumCount);

        var userId = 1L;

        //when & then
        assertThatThrownBy(() -> DataInitializerFactory.reservation(userId, shopReservation, personCount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("예약 인원 수[" + personCount + "]가 매장[" + shopId + "]에서 정한 " +
                        "최소 최대 인원 범위에서 벗어납니다." +
                        "[" + minimumCount + " ~ " + maximumCount + "]");
    }

    @ParameterizedTest
    @DisplayName("예약의 상태를 변경 할 수 있다.")
    @EnumSource(value = ReservationStatus.class, mode = EnumSource.Mode.EXCLUDE, names = {"CREATED"})
    void updateReservationStatusTest(ReservationStatus reservationStatus) {
        //given
        var shopId = 1L;
        var minimumCount = 2;
        var maximumCount = 30;
        var shopReservation = DataInitializerFactory.shopReservation(shopId, minimumCount, maximumCount);

        var userId = 1L;
        var personCount = 3;
        var reservation = DataInitializerFactory.reservation(userId, shopReservation, personCount);

        //when
        reservation.updateReservationStatus(reservationStatus);

        //then
        assertThat(reservation.getReservationStatus()).isEqualTo(reservationStatus);
    }

    @Test
    @DisplayName("동일한 예약 상태로 변경 할 수 없다.")
    void updateReservationStatusExTest() {
        //given
        var shopId = 1L;
        var minimumCount = 2;
        var maximumCount = 30;
        var shopReservation = DataInitializerFactory.shopReservation(shopId, minimumCount, maximumCount);

        var userId = 1L;
        var personCount = 3;
        var reservation = DataInitializerFactory.reservation(userId, shopReservation, personCount);

        //when & then
        assertThatThrownBy(() -> reservation.updateReservationStatus(ReservationStatus.CREATED))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("예약 상태를 동일한 상태로 변경 할 수 없습니다.");
    }

    @ParameterizedTest
    @DisplayName("CREATED 상태가 아닌 예약 상태는 변경 할 수 없다.")
    @EnumSource(value = ReservationStatus.class, mode = EnumSource.Mode.EXCLUDE, names = {"CREATED"})
    void updateReservationStatusNotCreatedExTest(ReservationStatus reservationStatus) {
        //given
        var shopId = 1L;
        var minimumCount = 2;
        var maximumCount = 30;
        var shopReservation = DataInitializerFactory.shopReservation(shopId, minimumCount, maximumCount);

        var userId = 1L;
        var personCount = 3;
        var reservation = DataInitializerFactory.reservation(userId, shopReservation, personCount);

        reservation.updateReservationStatus(reservationStatus);

        //when & then
        assertThatThrownBy(() -> reservation.updateReservationStatus(ReservationStatus.CREATED))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("예약이 CREATED 상태에서만 상태변경이 가능합니다.");
    }

    @Test
    @DisplayName("예약 좌석의 수가 예약 인원보다 크다면 예외가 발생한다.")
    void isSeatSizeUnderOrSamePersonCountFalseTest() {
        //given
        var shopId = 1L;
        var minimumCount = 2;
        var maximumCount = 30;
        var shopReservation = DataInitializerFactory.shopReservation(shopId, minimumCount, maximumCount);

        var userId = 1L;
        var personCount = 3;
        var reservation = DataInitializerFactory.reservation(userId, shopReservation, personCount);

        var shopReservationDateTimeSeatsSize = 4;

        //when & then
        assertThatThrownBy(() -> reservation.validSeatSizeAndPersonCount(shopReservationDateTimeSeatsSize))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("예약하려는 좌석의 수가 예약 인원 수를 초과했습니다. seats size : " + shopReservationDateTimeSeatsSize + ", person count : " + personCount);
    }
}