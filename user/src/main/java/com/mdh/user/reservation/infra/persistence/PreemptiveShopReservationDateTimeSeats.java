package com.mdh.user.reservation.infra.persistence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class PreemptiveShopReservationDateTimeSeats {

    private static final String KEY = "preemptive_shop_reservation_date_time_seats";
    private final StringRedisTemplate redisTemplate;

    public Optional<Boolean> contains(Long shopReservationDateTimeSeatId) {
        Boolean isMember = redisTemplate.opsForSet().isMember(KEY, String.valueOf(shopReservationDateTimeSeatId));
        return Optional.ofNullable(isMember);
    }

    public void addAll(List<Long> shopReservationDateTimeSeatIds) {
        shopReservationDateTimeSeatIds.forEach(shopReservationDateTimeSeatId ->
            redisTemplate.opsForSet().add(KEY, String.valueOf(shopReservationDateTimeSeatId)));
    }

    public void removeAll(List<Long> shopReservationDateTimeSeatIds) {
        shopReservationDateTimeSeatIds.forEach(shopReservationDateTimeSeatId ->
            redisTemplate.opsForSet().remove(KEY, String.valueOf(shopReservationDateTimeSeatId)));
    }
}
