package com.mdh.devtable.ownerreservation.application;

import com.mdh.devtable.DataInitializerFactory;
import com.mdh.devtable.ownerreservation.write.application.OwnerReservationService;
import com.mdh.devtable.ownerreservation.write.application.OwnerReservationServiceValidator;
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
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerReservationServiceTest {

    @InjectMocks
    private OwnerReservationService ownerReservationService;

    @Mock
    private OwnerReservationRepository ownerReservationRepository;

    @Mock
    private OwnerReservationServiceValidator ownerReservationServiceValidator;


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

    @DisplayName("점주는 매장의 예약 날짜에 해당하는 좌석을 생성할 수 있다. - 유효한 경우")
    @Test
    void createShopReservationDateTimeSeat_Valid() {
        // given
        var shopReservationDateTimeId = 1L;
        var seatId = 1L;
        var shopReservationDateTime = mock(ShopReservationDateTime.class);
        var seat = mock(Seat.class);

        when(ownerReservationRepository.findShopReservationDateTimeById(shopReservationDateTimeId))
                .thenReturn(Optional.of(shopReservationDateTime));
        when(ownerReservationRepository.findSeatById(seatId))
                .thenReturn(Optional.of(seat));
        when(ownerReservationRepository.saveShopReservationDateTimeSeat(any(ShopReservationDateTimeSeat.class)))
                .thenReturn(1L);

        // when
        var result = ownerReservationService.createShopReservationDateTimeSeat(shopReservationDateTimeId, seatId);

        // then
        assertThat(result).isEqualTo(1L);
        verify(ownerReservationServiceValidator).validateCreateShopReservationDateTimeSeat(shopReservationDateTimeId, seatId);
        verify(ownerReservationRepository).saveShopReservationDateTimeSeat(any(ShopReservationDateTimeSeat.class));
    }

    @DisplayName("점주는 매장의 예약 날짜에 해당하는 좌석을 생성할 수 없다. - 유효하지 않은 경우")
    @Test
    void createShopReservationDateTimeSeat_Invalid() {
        // given
        var shopReservationDateTimeId = 1L;
        var seatId = 1L;

        doThrow(new IllegalArgumentException(String.format("해당 날짜의 해당 좌석은 이미 예약 되었습니다. 날짜 id: %d, 좌석 id: %d", shopReservationDateTimeId, seatId)))
                .when(ownerReservationServiceValidator).validateCreateShopReservationDateTimeSeat(shopReservationDateTimeId, seatId);

        // when & then
        assertThatThrownBy(() -> ownerReservationService.createShopReservationDateTimeSeat(shopReservationDateTimeId, seatId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(String.format("해당 날짜의 해당 좌석은 이미 예약 되었습니다. 날짜 id: %d, 좌석 id: %d", shopReservationDateTimeId, seatId));

        verify(ownerReservationServiceValidator).validateCreateShopReservationDateTimeSeat(shopReservationDateTimeId, seatId);
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