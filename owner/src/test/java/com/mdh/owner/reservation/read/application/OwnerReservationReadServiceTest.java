package com.mdh.owner.reservation.read.application;

import com.mdh.common.reservation.ReservationStatus;
import com.mdh.common.reservation.SeatType;
import com.mdh.common.reservation.persistence.dto.OwnerShopReservationInfoResponse;
import com.mdh.owner.reservation.read.application.OwnerReservationReadService;
import com.mdh.owner.reservation.read.infra.persistence.OwnerReservationReadRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerReservationReadServiceTest {

    @Mock
    private OwnerReservationReadRepository ownerReservationReadRepository;

    @InjectMocks
    private OwnerReservationReadService ownerReservationReadService;

    @Test
    @DisplayName("점주는 매장 ID로 모든 예약 정보를 조회할 수 있다.")
    void findAllShopReservationByShopId() {
        //given
        Long shopId = 1L;
        var reservationStatus = ReservationStatus.CANCEL;
        var mockResponse = new OwnerShopReservationInfoResponse(
                "requirement", // requirement
                LocalDate.of(2023, 9, 12), // reservationDate
                LocalTime.of(12, 30), // reservationTime
                ReservationStatus.CANCEL, // reservationStatus
                4, // personCount
                SeatType.BAR // seatType
        );
        List<OwnerShopReservationInfoResponse> mockResponseList = Collections.singletonList(mockResponse);

        when(ownerReservationReadRepository.findAllReservationsByOwnerIdAndStatus(any(Long.class), any(ReservationStatus.class))).thenReturn(mockResponseList);

        //when
        var result = ownerReservationReadService.findAllReservationsByOwnerIdAndStatus(shopId, reservationStatus);

        //then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);

        verify(ownerReservationReadRepository, times(1)).findAllReservationsByOwnerIdAndStatus(shopId, reservationStatus);
    }
}