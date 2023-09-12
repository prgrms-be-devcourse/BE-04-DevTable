package com.mdh.devtable.ownerreservation.application;

import com.mdh.devtable.DataInitializerFactory;
import com.mdh.devtable.ownerreservation.infra.persistence.OwnerReservationRepository;
import com.mdh.devtable.ownerreservation.presentation.dto.SeatCreateRequest;
import com.mdh.devtable.ownerreservation.presentation.dto.ShopReservationCreateRequest;
import com.mdh.devtable.reservation.domain.Seat;
import com.mdh.devtable.reservation.domain.SeatType;
import com.mdh.devtable.reservation.domain.ShopReservation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        var seatCreateRequest = new SeatCreateRequest(seatType);
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
}