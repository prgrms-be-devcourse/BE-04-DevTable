package com.mdh.owner.reservation.write.infra.eventbus;

import com.mdh.common.reservation.domain.Reservation;
import com.mdh.common.reservation.domain.event.ReservationChangedAsCancelEvent;
import com.mdh.common.reservation.domain.event.ReservationChangedAsNoShowEvent;
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
class SendAlarmAfterChangedReservationStatusAsNoShowHandlerTest {

    @InjectMocks
    private SendAlarmAfterChangedReservationStatusAsNoShowHandler eventHandler;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ReservationRepository reservationRepository;

    @DisplayName("점주에 의해 예약이 노쇼 처리된 이후 알람이 발송된다.")
    @Test
    void sendAlarmAfterReservationCanceled() {

        // Given
        var reservationId = 1L;
        var reservation = mock(Reservation.class);
        var event = new ReservationChangedAsNoShowEvent(reservation);

        var alarmInfo = new ReservationAlarmInfo(1L, "test", LocalDate.now(), LocalTime.now(), 1, "test", "test");

        when(reservation.getId()).thenReturn(reservationId);
        when(reservationRepository.findReservationAlarmInfoById(reservationId)).thenReturn(Optional.of(alarmInfo));
        when(redisTemplate.convertAndSend(any(), any())).thenReturn(1L);

        // When
        eventHandler.handle(event);

        // Then
        verify(redisTemplate, times(1)).convertAndSend(any(), any());
        verify(reservationRepository, times(1)).findReservationAlarmInfoById(reservationId);
    }
}