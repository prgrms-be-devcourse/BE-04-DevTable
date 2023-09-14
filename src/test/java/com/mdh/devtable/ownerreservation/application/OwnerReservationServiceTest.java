package com.mdh.devtable.ownerreservation.application;

import com.mdh.devtable.DataInitializerFactory;
import com.mdh.devtable.ownerreservation.infra.persistence.OwnerReservationRepository;
import com.mdh.devtable.ownerreservation.presentation.dto.SeatCreateRequest;
import com.mdh.devtable.ownerreservation.presentation.dto.ShopReservationCreateRequest;
import com.mdh.devtable.ownerreservation.presentation.dto.ShopReservationDateTimeCreateRequest;
import com.mdh.devtable.reservation.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerReservationServiceTest {

    @InjectMocks
    private OwnerReservationService ownerReservationService;

    @Mock
    private OwnerReservationRepository ownerReservationRepository;


    @DisplayName("매장의 예약 정보를 생성할 수 있다.")
    @Test
    void createShopReservation() {
        // given
        Long shopId = 1L;
        var request = new ShopReservationCreateRequest(5, 1);
        when(ownerReservationRepository.saveShopReservation(any(ShopReservation.class))).thenReturn(1L);

        // when
        Long result = ownerReservationService.createShopReservation(shopId, request);

        // then
        assertThat(result).isEqualTo(1L);
        verify(ownerReservationRepository, times(1)).saveShopReservation(any(ShopReservation.class));
    }

    @DisplayName("매장의 좌석 정보를 생성할 수 있다.")
    @Test
    void saveSeat() {
        // given
        var shopId = 1L;
        var seatType = SeatType.BAR;
        var seatCreateRequest = new SeatCreateRequest(seatType, 5);
        ShopReservation shopReservation = DataInitializerFactory.shopReservation(shopId, 1, 5);

        when(ownerReservationRepository.findShopReservationByShopId(shopId))
                .thenReturn(Optional.of(shopReservation));
        when(ownerReservationRepository.saveSeat(any(Seat.class))).thenReturn(1L);

        // when
        Long result = ownerReservationService.saveSeat(shopId, seatCreateRequest);

        // then
        assertThat(result).isEqualTo(1L);
        verify(ownerReservationRepository, times(1)).findShopReservationByShopId(shopId);
        verify(ownerReservationRepository, times(1)).saveSeat(any(Seat.class));
    }

    @DisplayName("매장 예약 날짜 시간을 생성할 수 있다.")
    @Test
    void createShopReservationDateTime() {
        // given
        Long shopId = 1L;
        var localDate = LocalDate.of(2023, 9, 12);
        var localTime = LocalTime.of(12, 30);
        var request = new ShopReservationDateTimeCreateRequest(localDate, localTime);

        when(ownerReservationRepository.findShopReservationByShopId(shopId))
                .thenReturn(Optional.of(new ShopReservation(shopId, 1, 5)));
        when(ownerReservationRepository.saveShopReservationDateTime(any(ShopReservationDateTime.class)))
                .thenReturn(1L);

        // when
        Long result = ownerReservationService.createShopReservationDateTime(shopId, request);

        // then
        assertThat(result).isEqualTo(1L);
        verify(ownerReservationRepository, times(1)).saveShopReservationDateTime(any(ShopReservationDateTime.class));
    }

    @DisplayName("점주는 매장의 예약 날짜에 해당하는 좌석을 생성할 수 있다.")
    @Test
    void createShopReservationDateTimeSeat() {
        // given
        var shopReservationDateTimeId = 1L;
        var seatId = 1L;
        var shopId = 1L;
        var shopReservation = DataInitializerFactory.shopReservation(shopId, 1, 5);
        var shopReservationDateTime = DataInitializerFactory.shopReservationDateTime(shopReservation, LocalDate.now(), LocalTime.now());
        var seat = DataInitializerFactory.seat(shopReservation);

        when(ownerReservationRepository.findShopReservationDateTimeById(any(Long.class)))
                .thenReturn(Optional.of(shopReservationDateTime));
        when(ownerReservationRepository.findSeatById(any(Long.class)))
                .thenReturn(Optional.of(seat));
        when(ownerReservationRepository.saveShopReservationDateTimeSeat(any(ShopReservationDateTimeSeat.class)))
                .thenReturn(1L);

        // when
        var result = ownerReservationService.createShopReservationDateTimeSeat(shopReservationDateTimeId, seatId);

        // then
        assertThat(result).isEqualTo(1L);
        verify(ownerReservationRepository, times(1)).saveShopReservationDateTimeSeat(any(ShopReservationDateTimeSeat.class));
    }
}