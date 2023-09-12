package com.mdh.devtable.reservation.application;

import com.mdh.devtable.DataInitializerFactory;
import com.mdh.devtable.reservation.infra.persistence.ShopReservationDateTimeSeatRepository;
import com.mdh.devtable.reservation.infra.persistence.ShopReservationRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReservationValidatorTest {

    @InjectMocks
    private ReservationValidator reservationValidator;

    @Mock
    private ShopReservationRepository shopReservationRepository;

    @Mock
    private ShopReservationDateTimeSeatRepository shopReservationDateTimeSeatRepository;

    @Test
    @DisplayName("매장 웨이팅 정보를 반환한다.")
    void validShopWaitingTest() {
        //given
        var shopId = 1L;
        var shopReservation = DataInitializerFactory.shopReservation(shopId, 2, 10);

        given(shopReservationRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(shopReservation));

        //when
        var findShopReservation = reservationValidator.validShopReservation(shopId);

        //then
        verify(shopReservationRepository, times(1)).findById(any(Long.class));
        assertThat(findShopReservation).isEqualTo(shopReservation);
    }

    @Test
    @DisplayName("해당 매장 웨이팅 정보가 없으면 예외를 던진다.")
    void validShopWaitingExTest() {
        //given
        var shopId = 1L;

        given(shopReservationRepository.findById(any(Long.class))).willReturn(Optional.empty());

        //when & then
        Assertions.assertThatThrownBy(() -> reservationValidator.validShopReservation(shopId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("매장의 예약 정보가 없습니다. shopId " + shopId);
    }

    @Test
    @DisplayName("예약 좌석 정보들을 반환한다.")
    void validShopReservationDateTimeSeatTest() {
        //given
        var shopId = 1L;
        var shopReservationDateTimeSeatIds = List.of(1L, 2L, 3L);

        var shopReservation = DataInitializerFactory.shopReservation(shopId, 2, 10);

        var seat = DataInitializerFactory.seat(shopReservation);
        var shopReservationDateTime = DataInitializerFactory.shopReservationDateTime(shopReservation);

        var shopReservationDateTimeSeat = DataInitializerFactory.shopReservationDateTimeSeat(shopReservationDateTime, seat);

        given(shopReservationDateTimeSeatRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(shopReservationDateTimeSeat));

        //when
        var findShopReservationDateTimeSeats = reservationValidator.validShopReservationDateTimeSeats(shopReservationDateTimeSeatIds);

        //then
        verify(shopReservationDateTimeSeatRepository, times(3)).findById(any(Long.class));
        assertThat(findShopReservationDateTimeSeats).hasSize(3);
    }

    @Test
    @DisplayName("해당 예약 좌석이 없으면 예외를 던진다.")
    void validShopReservationDateTimeSeatExTest() {
        //given
        var shopReservationDateTimeSeatId1 = 1L;
        var shopReservationDateTimeSeatId2 = 1L;
        var shopReservationDateTimeSeatIds = List.of(shopReservationDateTimeSeatId1, shopReservationDateTimeSeatId2);

        given(shopReservationDateTimeSeatRepository.findById(any(Long.class))).willReturn(Optional.empty());

        //when & then
        Assertions.assertThatThrownBy(() -> reservationValidator.validShopReservationDateTimeSeats(shopReservationDateTimeSeatIds))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("예약 좌석 정보가 없습니다. shopReservationDateTimeSeatId : " + shopReservationDateTimeSeatId1);
    }
}