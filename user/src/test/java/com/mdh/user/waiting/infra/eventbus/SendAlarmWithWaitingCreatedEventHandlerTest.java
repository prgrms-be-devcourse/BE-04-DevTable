package com.mdh.user.waiting.infra.eventbus;

import com.mdh.common.waiting.domain.Waiting;
import com.mdh.common.waiting.persistence.WaitingRepository;
import com.mdh.common.waiting.persistence.dto.WaitingAlarmInfo;
import com.mdh.common.waiting.domain.event.WaitingCreatedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SendAlarmWithWaitingCreatedEventHandlerTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private WaitingRepository waitingRepository;

    @InjectMocks
    private SendAlarmWithWaitingCreatedEventHandler eventHandler;


    @Test
    void shouldSendAlarmAfterWaitingCreatedEvent() {
        // Given
        Long waitingId = 1L;
        var userId = 1L;
        var waiting = mock(Waiting.class);
        var event = new WaitingCreatedEvent(waiting);

        var alarmInfo = new WaitingAlarmInfo("test", "test", 1, 1, "01012345678", userId);

        when(waiting.getId()).thenReturn(1L);
        when(waitingRepository.findWaitingAlarmInfoById(waitingId)).thenReturn(Optional.of(alarmInfo));
        when(redisTemplate.convertAndSend(any(), any())).thenReturn(1L);

        // When
        eventHandler.sendAlarmAfterWaitingCreatedEvent(event);

        // Then
        verify(redisTemplate, times(1)).convertAndSend(any(), any());
        verify(waitingRepository, times(1)).findWaitingAlarmInfoById(waitingId);
    }

}