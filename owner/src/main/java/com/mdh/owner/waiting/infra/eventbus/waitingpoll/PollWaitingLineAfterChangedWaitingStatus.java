package com.mdh.owner.waiting.infra.eventbus.waitingpoll;

import com.mdh.common.waiting.domain.event.WaitingStatusChangedAsCanceledEvent;
import com.mdh.common.waiting.domain.event.WaitingStatusChangedAsNoShowEvent;
import com.mdh.common.waiting.domain.event.WaitingStatusChangedAsVisitedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class PollWaitingLineAfterChangedWaitingStatus {

    private final WaitingLinePoller waitingLinePoller;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(WaitingStatusChangedAsVisitedEvent event) {
        waitingLinePoller.pollWaitingLine(event.waiting());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(WaitingStatusChangedAsNoShowEvent event) {
        waitingLinePoller.pollWaitingLine(event.waiting());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(WaitingStatusChangedAsCanceledEvent event) {
        waitingLinePoller.pollWaitingLine(event.waiting());
    }
}