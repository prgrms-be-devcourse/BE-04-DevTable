package com.mdh.devtable.ownerreservation.application;

import com.mdh.devtable.DataInitializerFactory;
import com.mdh.devtable.ownerreservation.write.application.OwnerReservationService;
import com.mdh.devtable.ownerreservation.write.infra.persistence.OwnerReservationRepository;
import com.mdh.devtable.ownerreservation.write.presentation.dto.SeatCreateRequest;
import com.mdh.devtable.ownerreservation.write.presentation.dto.ShopReservationCreateRequest;
import com.mdh.devtable.ownerreservation.write.presentation.dto.ShopReservationDateTimeCreateRequest;
import com.mdh.devtable.reservation.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
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

    @DisplayName("매장 예약 날짜와 좌석을 생성할 수 있다.")
    @Test
    void createShopReservationDateTime() {
        // given
        var shopId = 1L;
        var localDate = LocalDate.of(2023, 9, 14);
        var localTime = LocalTime.of(12, 0);
        var request = new ShopReservationDateTimeCreateRequest(localDate, localTime);

        var shopReservation = DataInitializerFactory.shopReservation(shopId, 1, 5);
        var seat1 = DataInitializerFactory.seat(shopReservation);
        var seat2 = DataInitializerFactory.seat(shopReservation);
        var seats = List.of(seat1, seat2);

        given(ownerReservationRepository.findShopReservationByShopId(shopId)).willReturn(Optional.of(shopReservation));
        given(ownerReservationRepository.findAllSeatsByShopId(shopId)).willReturn(seats);
        given(ownerReservationRepository.saveShopReservationDateTime(any(ShopReservationDateTime.class))).willReturn(1L);

        // when
        var result = ownerReservationService.createShopReservationDateTime(shopId, request);

        // then
        assertThat(result).isEqualTo(1L);
        verify(ownerReservationRepository, times(1)).saveShopReservationDateTime(any(ShopReservationDateTime.class));
        verify(ownerReservationRepository, times(1)).saveAllShopReservationDateTimeSeat(any(List.class));
    }

    @DisplayName("예약을 취소할 수 있다.")
    @Test
    void cancelReservation() {
        // given
        var reservationId = 1L;
        var reservation = mock(Reservation.class); // Assuming Reservation is the class you're using
        when(ownerReservationRepository.findReservationById(reservationId)).thenReturn(Optional.of(reservation));

        // when
        ownerReservationService.cancelReservationByOwner(reservationId);

        // then
        verify(reservation, times(1)).updateReservationStatus(ReservationStatus.CANCEL);
        verify(ownerReservationRepository, times(1)).findReservationById(reservationId);
    }

    @DisplayName("점주는 예약을 방문 완료로 표시할 수 있다.")
    @Test
    void markReservationAsVisited() {
        // given
        var reservationId = 1L;
        var mockReservation = mock(Reservation.class);
        when(ownerReservationRepository.findReservationById(reservationId))
                .thenReturn(Optional.of(mockReservation));

        // when
        ownerReservationService.markReservationAsVisitedByOwner(reservationId);

        // then
        verify(mockReservation, times(1)).updateReservationStatus(ReservationStatus.VISITED);
    }

    @DisplayName("점주는 예약을 노쇼로 표시할 수 있다.")
    @Test
    void markReservationAsNoShowByOwner() {
        // given
        var reservationId = 1L;
        var reservation = mock(Reservation.class);

        given(ownerReservationRepository.findReservationById(any(Long.class)))
                .willReturn(Optional.of(reservation));

        // when
        ownerReservationService.markReservationAsNoShowByOwner(reservationId);

        // then
        verify(ownerReservationRepository).findReservationById(reservationId);
        verify(reservation).updateReservationStatus(ReservationStatus.NO_SHOW);
    }

}