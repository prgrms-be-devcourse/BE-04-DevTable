package com.mdh.common.reservation.domain.event;

import com.mdh.common.reservation.domain.Reservation;

public record ReservationChangedAsVisitedEvent(
        Reservation reservation
) {
}