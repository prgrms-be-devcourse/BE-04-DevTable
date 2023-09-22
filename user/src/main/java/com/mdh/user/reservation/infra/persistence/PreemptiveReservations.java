package com.mdh.user.reservation.infra.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import com.mdh.user.reservation.application.dto.ReservationRedisDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PreemptiveReservations {

    private static final String KEY = "preemptive_reservation";
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public Optional<Boolean> contains(UUID reservationId) {
        Boolean hasKey = redisTemplate.opsForHash().hasKey(KEY, reservationId.toString());
        return Optional.ofNullable(hasKey);
    }

    public ReservationRedisDto get(UUID reservationId) {
        try {
            var hashOperations = redisTemplate.opsForHash();
            return objectMapper.readValue(String.valueOf(hashOperations.get(KEY, reservationId.toString())), ReservationRedisDto.class);
        } catch (Exception e) {
            log.debug("Json을 ReservationRedisDto.class로 변환에 실패 ", e);
            throw new RuntimeJsonMappingException("Json을 ReservationRedisDto.class로 변환하는데 실패했습니다.");
        }
    }

    public void add(UUID reservationId, ReservationRedisDto reservationRedisDto) throws JsonProcessingException {
        var hashOperations = redisTemplate.opsForHash();
        String reservationOfString = objectMapper.writeValueAsString(reservationRedisDto);
        hashOperations.put(KEY, reservationId.toString(), reservationOfString);
    }

    public void remove(UUID reservationId) {
        var hashOperations = redisTemplate.opsForHash();
        hashOperations.delete(KEY, reservationId.toString());
    }
}