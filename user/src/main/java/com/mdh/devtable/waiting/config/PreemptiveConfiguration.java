package com.mdh.devtable.waiting.config;

import com.mdh.devtable.reservation.Reservation;
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
