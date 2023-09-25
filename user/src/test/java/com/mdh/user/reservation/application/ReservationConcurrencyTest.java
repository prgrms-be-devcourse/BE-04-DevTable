package com.mdh.user.reservation.application;

import com.mdh.user.reservation.presentation.dto.ReservationPreemptiveRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
public class ReservationConcurrencyTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private Logger log = LoggerFactory.getLogger(ReservationConcurrencyTest.class);

    private static final String SEAT_KEY = "preemptive_shop_reservation_date_time_seats";
    private static final String RESERVATION_KEY = "preemptive_reservation";

    @BeforeEach
    void setup() {
        redisTemplate.delete(SEAT_KEY);
        redisTemplate.delete(RESERVATION_KEY);
    }

    @Test
    @DisplayName("여러 클라이언트가 동일한 예약 좌석에 대해 동시에 선점할 때 동시성 문제가 발생한다.")
    void concurrencyTest() {
        var es = Executors.newFixedThreadPool(30);

        var latch = new CountDownLatch(30);
        var request = new ReservationPreemptiveRequest(List.of(1L, 2L), "require", 2);
//        System.out.println(reservationService.preemtiveReservation(1L, request));
        AtomicInteger ai = new AtomicInteger();
        for (int i = 0; i < 30; i++) {
            es.execute(() -> {
                try {
                    log.info("생성된 예약 아이디 = {}", reservationService.preemtiveReservation(1L, request));
                    log.info("===============현재 횟수" + ai.getAndIncrement());
                } finally {
                    latch.countDown();
                }


            });
        }

        try {
            // 모든 스레드가 완료될 때까지 대기
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            es.shutdown();
        }
    }

}
