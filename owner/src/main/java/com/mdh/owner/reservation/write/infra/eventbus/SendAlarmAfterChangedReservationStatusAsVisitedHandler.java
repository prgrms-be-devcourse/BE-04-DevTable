package com.mdh.owner.reservation.write.infra.eventbus;

import com.mdh.common.reservation.domain.event.ReservationChangedAsVisitedEvent;
import com.mdh.common.reservation.persistence.ReservationRepository;
import com.mdh.owner.global.message.AlarmMessage;
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
public class SendAlarmAfterChangedReservationStatusAsVisitedHandler {

    @Value("${spring.data.redis.topic.alarm}")
    private String topic;

    private final StringRedisTemplate redisTemplate;
    private final ReservationRepository reservationRepository;

    @Async
    @Transactional
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ReservationChangedAsVisitedEvent event) {
        var reservation = event.reservation();

        var reservationAlarmInfo = reservationRepository
                .findReservationAlarmInfoById(reservation.getId())
                .orElseThrow(() -> new NoSuchElementException("존재하는 예약 정보가 없습니다. " + reservation.getReservationId()));

        redisTemplate.convertAndSend(topic,
                new AlarmMessage(String.valueOf(reservationAlarmInfo.userId()),
                        reservationAlarmInfo.shopName(),
                        "점주에 의해 예약이 방문처리 됐습니다."));
    }

}