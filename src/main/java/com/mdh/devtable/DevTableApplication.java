package com.mdh.devtable;

import com.mdh.devtable.reservation.domain.Reservation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.*;

@SpringBootApplication
public class DevTableApplication {

    public static void main(String[] args) {
        SpringApplication.run(DevTableApplication.class, args);
    }

    @Bean
    public Set<Long> preemtiveShopReservationDateTimeSeats() {
        return new HashSet<>();
    }

    @Bean
    public Map<UUID, Reservation> preemptiveReservation() {
        return new HashMap<>();
    }
}