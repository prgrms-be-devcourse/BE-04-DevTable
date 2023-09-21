package com.mdh.user.waiting.infra.eventbus;

import com.mdh.common.waiting.domain.event.WaitingPostponedEvent;
import com.mdh.common.waiting.persistence.WaitingRepository;
import com.mdh.user.global.message.AlarmMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Component
public class SendAlarmWithWaitingPostponedEventHandler {

    @Value("${spring.data.redis.topic.alarm}")
    private String topic;

    private final StringRedisTemplate redisTemplate;
    private final WaitingRepository waitingRepository;

    @Async
    @Transactional(readOnly = true)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendAlarmAfterWaitingPostponedEvent(WaitingPostponedEvent event) {
        var waitingId = event.waiting().getId();
        var alarmInfo = waitingRepository.findWaitingAlarmInfoById(waitingId)
                .orElseThrow(() -> new NoSuchElementException("존재하는 웨이팅 정보가 없습니다: " + waitingId));

        var message = new AlarmMessage(String.valueOf(alarmInfo.userId()),
                alarmInfo.shopName(),
                alarmInfo.toString() + "웨이팅이 미뤄졌습니다.");
        redisTemplate.convertAndSend(topic, message);
    }
}