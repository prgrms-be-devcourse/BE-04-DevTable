package com.mdh.owner.waiting.infra.eventbus.waitingpoll;

import com.mdh.common.waiting.domain.event.WaitingStatusChangedAsCanceledEvent;
import com.mdh.common.waiting.domain.event.WaitingStatusChangedAsNoShowEvent;
import com.mdh.common.waiting.domain.event.WaitingStatusChangedAsVisitedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PollWaitingLineAfterChangedWaitingStatusTest {

    @InjectMocks
    private PollWaitingLineAfterChangedWaitingStatus eventHandler;

    @Mock
    private WaitingLinePoller waitingLinePoller;

    @Mock
    private WaitingStatusChangedAsVisitedEvent visitedEvent;

    @Mock
    private WaitingStatusChangedAsNoShowEvent noShowEvent;

    @Mock
    private WaitingStatusChangedAsCanceledEvent canceledEvent;


    @DisplayName("웨이팅 방문처리 이벤트 발생 시 pollWaitingLine 메소드가 호출된다.")
    @Test
    void shouldCallPollWaitingLineOnVisitedEvent() {
        eventHandler.handle(visitedEvent);
        verify(waitingLinePoller, times(1)).pollWaitingLine(visitedEvent.waiting());
    }

    @DisplayName("웨이팅 노쇼처리 이벤트 발생 시 pollWaitingLine 메소드가 호출된다.")
    @Test
    void shouldCallPollWaitingLineOnNoShowEvent() {
        eventHandler.handle(noShowEvent);
        verify(waitingLinePoller, times(1)).pollWaitingLine(noShowEvent.waiting());
    }

    @DisplayName("웨이팅 취소처리 이벤트 발생 시 pollWaitingLine 메소드가 호출된다.")
    @Test
    void shouldCallPollWaitingLineOnCanceledEvent() {
        eventHandler.handle(canceledEvent);
        verify(waitingLinePoller, times(1)).pollWaitingLine(canceledEvent.waiting());
    }
}