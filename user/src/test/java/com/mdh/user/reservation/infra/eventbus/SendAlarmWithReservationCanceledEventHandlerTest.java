package com.mdh.user.reservation.infra.eventbus;

import com.mdh.common.reservation.domain.Reservation;
import com.mdh.common.reservation.domain.event.ReservationCanceledEvent;
import com.mdh.common.reservation.persistence.ReservationRepository;
import com.mdh.common.reservation.persistence.dto.ReservationAlarmInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SendAlarmWithReservationCanceledEventHandlerTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private SendAlarmWithReservationCanceledEventHandler eventHandler;

    @DisplayName("예약이 취소된 이후 취소 알람이 발송된다.")
    @Test
    void sendAlarmAfterReservationCanceled() {

        // Given
        Long reservationId = 1L;
        var userId = 1L;
        var reservation = mock(Reservation.class);
        var event = new ReservationCanceledEvent(reservation);

        var alarmInfo = new ReservationAlarmInfo(1L, "test", LocalDate.now(), LocalTime.now(), 1, "test", "test");

        when(reservation.getId()).thenReturn(reservationId);
        when(reservationRepository.findReservationAlarmInfoById(reservationId)).thenReturn(Optional.of(alarmInfo));
        when(redisTemplate.convertAndSend(any(), any())).thenReturn(1L);

        // When
        eventHandler.sendAlarmAfterReservationCanceled(event);

        // Then
        verify(redisTemplate, times(1)).convertAndSend(any(), any());
        verify(reservationRepository, times(1)).findReservationAlarmInfoById(reservationId);
    }
}