package com.mdh.user.reservation.config;

import com.mdh.common.reservation.domain.Reservation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
public class PreemptiveConfiguration {

    @Bean
    public Set<Long> preemptiveShopReservationDateTimeSeats() {
        return new HashSet<>();
    }

    @Bean
    public Map<UUID, Reservation> preemptiveReservation() {
        return new HashMap<>();
    }
}