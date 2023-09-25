package com.mdh.user.reservation.application;

import com.mdh.user.reservation.presentation.dto.ReservationPreemptiveRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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

//@Disabled
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
    @DisplayName("30명의 클라이언트가 동일한 예약 좌석에 대해 동시에 선점할 수 있다.")
    void concurrencyTest() {
        var es = Executors.newFixedThreadPool(30);

        var latch = new CountDownLatch(30);
        var request = new ReservationPreemptiveRequest(List.of(1L, 2L), "require", 2);
        var success = new AtomicInteger();
        var fail = new AtomicInteger();
        for (int i = 0; i < 30; i++) {
            es.execute(() -> {
                try {
                    reservationService.preemtiveReservation(1L, request);
                    log.info("성공 횟수" + success.incrementAndGet());
                } catch (Exception e) {
                    log.info("실패 횟수" + fail.incrementAndGet());
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

    @Test
    @DisplayName("여러 클라이언트가 서로 다른 예약 좌석에 동시에 요청하면 요청이 승인된다.")
    void concurrencyTest2() {
        var es = Executors.newFixedThreadPool(2); // 두명의 사용자를 위한 스레드 풀

        var latch = new CountDownLatch(2); // 두 스레드가 완료될 때까지 대기하기 위한 CountDownLatch

        var request = new ReservationPreemptiveRequest(List.of(1L, 2L), "require", 2);
        var request2 = new ReservationPreemptiveRequest(List.of(3L, 4L), "require", 2);

        // 첫 번째 사용자의 요청
        es.execute(() -> {
            try {
                log.info("생성된 예약 아이디 = {}", reservationService.preemtiveReservation(1L, request));
                log.info("첫번 째 사용자의 요청 성공");
            } catch (Exception e) {
                log.error("첫번 째 사용자의 요청 실패", e);
            } finally {
                latch.countDown();
            }
        });

        // 두 번째 사용자의 요청
        es.execute(() -> {
            try {
                log.info("생성된 예약 아이디 = {}", reservationService.preemtiveReservation(1L, request2));
                log.info("두번 째 사용자의 요청 성공");
            } catch (Exception e) {
                log.error("두번 째 사용자의 요청 실패", e);
            } finally {
                latch.countDown();
            }
        });

        try {
            latch.await(); // 두 스레드가 모두 완료될 때까지 대기
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            es.shutdown(); // 스레드 풀 종료
        }
    }

    @Test
    @DisplayName("여러 클라이언트가 서로 같은 예약 좌석에 동시에 요청하면 요청이 거절된다.")
    void concurrencyTest3() {
        var es = Executors.newFixedThreadPool(2); // 두명의 사용자를 위한 스레드 풀

        var latch = new CountDownLatch(2); // 두 스레드가 완료될 때까지 대기하기 위한 CountDownLatch

        var request = new ReservationPreemptiveRequest(List.of(1L, 2L), "require", 2);
        var request2 = new ReservationPreemptiveRequest(List.of(1L, 2L), "require", 2);

        // 첫 번째 사용자의 요청
        es.execute(() -> {
            try {
                log.info("생성된 예약 아이디 = {}", reservationService.preemtiveReservation(1L, request));
                log.info("첫번 째 사용자의 요청 성공");
            } catch (Exception e) {
                log.error("첫번 째 사용자의 요청 실패", e);
            } finally {
                latch.countDown();
            }
        });

        // 두 번째 사용자의 요청
        es.execute(() -> {
            try {
                log.info("생성된 예약 아이디 = {}", reservationService.preemtiveReservation(1L, request2));
                log.info("두번 째 사용자의 요청 성공");
            } catch (Exception e) {
                log.error("두번 째 사용자의 요청 실패", e);
            } finally {
                latch.countDown();
            }
        });

        try {
            latch.await(); // 두 스레드가 모두 완료될 때까지 대기
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            es.shutdown(); // 스레드 풀 종료
        }
    }

    @Test
    @DisplayName("30명의 클라이언트가 동일한 예약 좌석에 대해 동시에 선점할 때 1명의 사용자만 성공한다.")
    void concurrencyTest4() {
        var es = Executors.newFixedThreadPool(30);

        var latch = new CountDownLatch(30);
        var request = new ReservationPreemptiveRequest(List.of(1L, 2L), "require", 2);
        var success = new AtomicInteger();
        var fail = new AtomicInteger();
        for (int i = 0; i < 30; i++) {
            es.execute(() -> {
                try {
                    reservationService.preemtiveReservation(1L, request);
                    log.info("성공 횟수" + success.incrementAndGet());
                } catch (Exception e) {
                    log.info("실패 횟수" + fail.incrementAndGet());
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