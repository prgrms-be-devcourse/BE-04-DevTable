package com.mdh.user.reservation.infra.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mdh.common.reservation.domain.Reservation;
import com.mdh.user.DataInitializerFactory;
import com.mdh.user.reservation.application.dto.ReservationRedisDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Disabled
class ReservationCacheTest {

    @Autowired
    private ReservationCache reservationCache;

    @Autowired
    private PreemptiveReservations preemptiveReservations;

    @Autowired
    private PreemptiveShopReservationDateTimeSeats preemptiveShopReservationDateTimeSeats;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String SEAT_KEY = "preemptive_shop_reservation_date_time_seats";
    private static final String RESERVATION_KEY = "preemptive_reservation";

    @AfterEach
    void teardown() {
        redisTemplate.delete(SEAT_KEY);
        redisTemplate.delete(RESERVATION_KEY);
    }

    @Test
    @DisplayName("예약을 선점할 때 예약 좌석과 만들어둔 예약을 캐시에 저장한다.")
    void preempTest() {
        // given
        var shopReservationDateTimeSeatIds = List.of(1L, 2L, 3L);
        var reservationId = UUID.randomUUID();
        var reservation = DataInitializerFactory.preemptiveReservation(reservationId, 1L, 3);

        // when
        reservationCache.preemp(shopReservationDateTimeSeatIds, reservationId, reservation);

        // then
        assertThat(preemptiveReservations.contains(reservationId).orElse(null)).isTrue();
        shopReservationDateTimeSeatIds.stream().forEach(shopReservationDateTimeSeatId -> {
            Boolean contains = preemptiveShopReservationDateTimeSeats.contains(shopReservationDateTimeSeatId).orElse(null);
            assertThat(contains).isTrue();
        });
    }

    @Test
    @DisplayName("이미 선점된 좌석이면 예외가 발생한다.")
    void preempExTest() {
        // given
        var shopReservationDateTimeSeatIds = List.of(1L, 2L, 3L);
        var reservationId = UUID.randomUUID();
        var reservation = DataInitializerFactory.preemptiveReservation(reservationId, 1L, 3);

        preemptiveShopReservationDateTimeSeats.addAll(shopReservationDateTimeSeatIds);

        // when & then
        assertThatThrownBy(() -> reservationCache.preemp(shopReservationDateTimeSeatIds, reservationId, reservation))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("이미 선점된 좌석이므로 선점할 수 없습니다.");
    }

    @Test
    @DisplayName("예약 확정 시 예약 좌석이 선점된 상태인지 확인하고 만들어둔 예약을 가져온다.")
    void registerTest() throws JsonProcessingException {
        // given
        var shopReservationDateTimeSeatIds = List.of(1L, 2L, 3L);
        var reservationId = UUID.randomUUID();
        var reservation = DataInitializerFactory.preemptiveReservation(reservationId, 1L, 3);
        var reservationRedisDto = ReservationRedisDto.of(reservation);

        preemptiveShopReservationDateTimeSeats.addAll(shopReservationDateTimeSeatIds);
        preemptiveReservations.add(reservationId, reservationRedisDto);

        // when
        Reservation registered = reservationCache.register(shopReservationDateTimeSeatIds, reservationId);

        // then
        assertThat(registered).usingRecursiveComparison().isEqualTo(reservation);
    }

    @Test
    @DisplayName("예약 확정 시 예약 좌석을 선점하지 않았다면 예외가 발생한다.")
    void registerNotFoundReservationSeatExTest() {
        // given
        var shopReservationDateTimeSeatIds = List.of(1L, 2L, 3L);
        var reservationId = UUID.randomUUID();

        // when & then
        assertThatThrownBy(() -> reservationCache.register(shopReservationDateTimeSeatIds, reservationId))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("선점된 좌석이 아니므로 예약 확정할 수 없습니다.");
    }

    @Test
    @DisplayName("예약 확정 시 존재하지 않는 예약이라면 예외가 발생한다.")
    void registerNotFoundReservationTest() {
        // given
        var shopReservationDateTimeSeatIds = List.of(1L, 2L, 3L);
        var reservationId = UUID.randomUUID();

        preemptiveShopReservationDateTimeSeats.addAll(shopReservationDateTimeSeatIds);

        // when & then
        assertThatThrownBy(() -> reservationCache.register(shopReservationDateTimeSeatIds, reservationId))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("선점된 예약이 아니므로 예약 확정할 수 없습니다.");
    }

    @Test
    @DisplayName("예약과 예약 좌석을 삭제한다.")
    void removeAllTest() throws JsonProcessingException {
        // given
        var shopReservationDateTimeSeatIds = List.of(1L, 2L, 3L);
        var reservationId = UUID.randomUUID();
        var reservation = DataInitializerFactory.preemptiveReservation(reservationId, 1L, 3);
        var reservationRedisDto = ReservationRedisDto.of(reservation);

        preemptiveShopReservationDateTimeSeats.addAll(shopReservationDateTimeSeatIds);
        preemptiveReservations.add(reservationId, reservationRedisDto);

        // when
        reservationCache.removeAll(shopReservationDateTimeSeatIds, reservationId);

        // then
        assertThat(preemptiveReservations.contains(reservationId).orElse(null)).isFalse();
        shopReservationDateTimeSeatIds.stream().forEach(shopReservationDateTimeSeatId -> {
            Boolean contains = preemptiveShopReservationDateTimeSeats.contains(shopReservationDateTimeSeatId).orElse(null);
            assertThat(contains).isFalse();
        });
    }
}