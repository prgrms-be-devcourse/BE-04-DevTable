package com.mdh.user.reservation.infra.persistence;

import com.mdh.common.reservation.domain.Reservation;
import com.mdh.user.reservation.application.dto.ReservationRedisDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationCache {

    private final PreemptiveShopReservationDateTimeSeats preemptiveShopReservationDateTimeSeats;

    private final PreemptiveReservations preemptiveReservations;

    private final StringRedisTemplate redisTemplate;

    private static final int TIME_TO_LEAVE = 9;

    public UUID preemp(List<Long> shopReservationDateTimeSeatIds, UUID reservationId, Reservation reservation) {
        shopReservationDateTimeSeatIds.forEach(i -> {
            var key = "lock" + i;
            var currentValue = redisTemplate.opsForValue().increment(key);
            if (currentValue != null && currentValue == 1) {
                redisTemplate.expire(key, TIME_TO_LEAVE, TimeUnit.MINUTES);
            }
            if (currentValue != null && currentValue >= 2) {
                throw new IllegalStateException("좌석 " + i + "의 선점 횟수가 2 이상입니다.");
            }
        });


        var reservationRedisDto = ReservationRedisDto.of(reservation);

        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                try {
                    operations.multi();
                    preemptiveShopReservationDateTimeSeats.addAll(shopReservationDateTimeSeatIds);
                    preemptiveReservations.add(reservationId, reservationRedisDto);
                } catch (Exception e) {
                    operations.discard();
                }
                return operations.exec();
            }
        });
        return reservationId;
    }

    public Reservation register(List<Long> shopReservationDateTimeSeatIds, UUID reservationId) {
        shopReservationDateTimeSeatIds.forEach(shopReservationDateTimeSeatId -> {
            var contains = preemptiveShopReservationDateTimeSeats.contains(shopReservationDateTimeSeatId)
                .orElseThrow(() -> new IllegalCallerException("파이프라인 혹은 트랜잭션에서 조회 시 null 값입니다."));
            if (Boolean.FALSE.equals(contains)) {
                throw new IllegalStateException("선점된 좌석이 아니므로 예약 확정할 수 없습니다.");
            }
        });
        var contains = preemptiveReservations.contains(reservationId)
            .orElseThrow(() -> new IllegalCallerException("파이프라인 혹은 트랜잭션에서 조회 시 null 값입니다."));
        if (Boolean.FALSE.equals(contains)) {
            throw new IllegalStateException("선점된 예약이 아니므로 예약 확정할 수 없습니다.");
        }
        var reservationRedisDto = preemptiveReservations.get(reservationId);
        return reservationRedisDto.toEntity();
    }

    public void removeAll(List<Long> shopReservationDateTimeSeatIds, UUID reservationId) {
        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                try {
                    operations.multi();
                    preemptiveShopReservationDateTimeSeats.removeAll(shopReservationDateTimeSeatIds);
                    preemptiveReservations.remove(reservationId);
                } catch (Exception e) {
                    operations.discard();
                }
                return operations.exec();
            }
        });
    }
}