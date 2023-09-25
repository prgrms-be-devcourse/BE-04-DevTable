package com.mdh.user.reservation.application.dto;

import com.mdh.common.reservation.domain.Reservation;

import java.util.UUID;

public record ReservationRedisDto(
    UUID reservationId,
    Long userId,
    String requirements,
    int personCount
) {
    public static ReservationRedisDto of(Reservation reservation) {
        return new ReservationRedisDto(
            reservation.getReservationId(),
            reservation.getUserId(),
            reservation.getRequirement(),
            reservation.getPersonCount()
        );
    }

    public Reservation toEntity() {
        return new Reservation(
            this.reservationId,
            this.userId,
            this.requirements,
            this.personCount
        );
    }
}