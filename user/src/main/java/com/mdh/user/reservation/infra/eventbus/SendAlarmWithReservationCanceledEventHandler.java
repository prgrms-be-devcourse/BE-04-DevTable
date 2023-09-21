package com.mdh.user.reservation.infra.eventbus;

import com.mdh.common.reservation.domain.event.ReservationCanceledEvent;
import com.mdh.common.reservation.persistence.ReservationRepository;
import com.mdh.user.global.message.AlarmMessage;
import io.micrometer.core.annotation.Counted;
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
public class SendAlarmWithReservationCanceledEventHandler {

    @Value("${spring.data.redis.topic.alarm}")
    private String topic;

    private final StringRedisTemplate redisTemplate;
    private final ReservationRepository reservationRepository;

    @Counted("alarm.user.reservation.cancel")
    @Async
    @Transactional(readOnly = true)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendAlarmAfterReservationCanceled(ReservationCanceledEvent event) {
        var reservation = event.reservation();

        var reservationAlarmInfo = reservationRepository
                .findReservationAlarmInfoById(reservation.getId())
                .orElseThrow(() -> new NoSuchElementException("존재하는 예약 정보가 없습니다. " + reservation.getReservationId()));

        redisTemplate.convertAndSend(topic,
                new AlarmMessage(String.valueOf(reservationAlarmInfo.userId()),
                        reservationAlarmInfo.shopName(),
                        "예약이 취소 됐습니다."));
    }
}