package com.mdh.user.reservation.application.dto;

import com.mdh.common.reservation.domain.Reservation;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class ReservationRedisDto {
    private UUID reservationId;
    private Long userId;
    private String requirements;
    private int personCount;

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