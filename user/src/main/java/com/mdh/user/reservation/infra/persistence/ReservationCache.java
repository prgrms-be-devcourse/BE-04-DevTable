package com.mdh.user.reservation.infra.persistence;

import com.mdh.common.reservation.domain.Reservation;
import com.mdh.user.reservation.application.dto.ReservationRedisDto;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReservationCache {

    private final PreemptiveShopReservationDateTimeSeats preemptiveShopReservationDateTimeSeats;

    private final PreemptiveReservations preemptiveReservations;

    private final StringRedisTemplate redisTemplate;

    public UUID preemp(List<Long> shopReservationDateTimeSeatIds, UUID reservationId, Reservation reservation) {
        shopReservationDateTimeSeatIds.forEach(shopReservationDateTimeSeatId -> {
            var contains = preemptiveShopReservationDateTimeSeats.contains(shopReservationDateTimeSeatId)
                .orElseThrow(() -> new IllegalCallerException("파이프라인 혹은 트랜잭션에서 조회 시 null 값입니다."));
            if (Boolean.TRUE.equals(contains)) {
                throw new IllegalStateException("이미 선점된 좌석이므로 선점할 수 없습니다.");
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
            Boolean contains = preemptiveShopReservationDateTimeSeats.contains(shopReservationDateTimeSeatId)
                .orElseThrow(() -> new IllegalCallerException("파이프라인 혹은 트랜잭션에서 조회 시 null 값입니다."));
            if (Boolean.FALSE.equals(contains)) {
                throw new IllegalStateException("선점된 좌석이 아니므로 예약 확정할 수 없습니다.");
            }
        });
        Boolean contains = preemptiveReservations.contains(reservationId)
            .orElseThrow(() -> new IllegalCallerException("파이프라인 혹은 트랜잭션에서 조회 시 null 값입니다."));
        if (Boolean.FALSE.equals(contains)) {
            throw new IllegalStateException("선점된 예약이 아니므로 예약 확정할 수 없습니다.");
        }
        ReservationRedisDto reservationRedisDto = preemptiveReservations.get(reservationId);
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