package com.mdh.owner.waiting.infra.eventbus;

import com.mdh.common.waiting.domain.Waiting;
import com.mdh.common.waiting.domain.event.WaitingStatusChangedAsNoShowEvent;
import com.mdh.common.waiting.persistence.WaitingRepository;
import com.mdh.common.waiting.persistence.dto.WaitingAlarmInfo;
import com.mdh.owner.waiting.infra.eventbus.alarm.SendAlarmAfterChangedWaitingStatusAsNoShowHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class SendAlarmAfterChangedWaitingStatusAsNoShowHandlerTest {

    @InjectMocks
    private SendAlarmAfterChangedWaitingStatusAsNoShowHandler eventHandler;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private WaitingRepository waitingRepository;

    @DisplayName("점주가 웨이팅 상태를 취소로 변경하면 손님에게 알람이 간다.")
    @Test
    void handle() {
        // Given
        Long waitingId = 1L;
        var userId = 1L;
        var waiting = mock(Waiting.class);
        var event = new WaitingStatusChangedAsNoShowEvent(waiting);

        var alarmInfo = new WaitingAlarmInfo("test", "test", 1, 1, "01012345678", userId);

        when(waiting.getId()).thenReturn(1L);
        when(waitingRepository.findWaitingAlarmInfoById(waitingId)).thenReturn(Optional.of(alarmInfo));
        when(redisTemplate.convertAndSend(any(), any())).thenReturn(1L);

        // When
        eventHandler.handle(event);

        // Then
        verify(redisTemplate, times(1)).convertAndSend(any(), any());
        verify(waitingRepository, times(1)).findWaitingAlarmInfoById(waitingId);
    }
}