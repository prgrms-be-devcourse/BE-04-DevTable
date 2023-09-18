package com.mdh.user.waiting.config;

import com.mdh.common.reservation.domain.Reservation;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
public class PreemptiveConfiguration {
    public Set<Long> preemptiveShopReservationDateTimeSeats() {
        return new HashSet<>();
    }

    public Map<UUID, Reservation> preemptiveReservation() {
        return new HashMap<>();
    }
}