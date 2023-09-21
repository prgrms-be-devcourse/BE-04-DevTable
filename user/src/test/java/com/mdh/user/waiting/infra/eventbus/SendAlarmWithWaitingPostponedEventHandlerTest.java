package com.mdh.user.waiting.infra.eventbus;

import com.mdh.common.waiting.domain.Waiting;
import com.mdh.common.waiting.domain.event.WaitingCreatedEvent;
import com.mdh.common.waiting.domain.event.WaitingPostponedEvent;
import com.mdh.common.waiting.persistence.WaitingRepository;
import com.mdh.common.waiting.persistence.dto.WaitingAlarmInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class SendAlarmWithWaitingPostponedEventHandlerTest {

    @InjectMocks
    private SendAlarmWithWaitingPostponedEventHandler eventHandler;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private WaitingRepository waitingRepository;

    @DisplayName("웨이팅이 미뤄지면 사용자에게 알람이 발생한다.")
    @Test
    void shouldSendAlarmAfterWaitingCreatedEvent() {
        // Given
        var waitingId = 1L;
        var userId = 1L;
        var waiting = mock(Waiting.class);
        var event = new WaitingPostponedEvent(waiting);

        var alarmInfo = new WaitingAlarmInfo("test", "test", 1, 1, "01012345678", userId);

        when(waiting.getId()).thenReturn(1L);
        when(waitingRepository.findWaitingAlarmInfoById(waitingId)).thenReturn(Optional.of(alarmInfo));
        when(redisTemplate.convertAndSend(any(), any())).thenReturn(1L);

        // When
        eventHandler.sendAlarmAfterWaitingPostponedEvent(event);

        // Then
        verify(redisTemplate, times(1)).convertAndSend(any(), any());
        verify(waitingRepository, times(1)).findWaitingAlarmInfoById(waitingId);
    }
}