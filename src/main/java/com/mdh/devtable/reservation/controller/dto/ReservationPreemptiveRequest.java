package com.mdh.devtable.reservation.controller.dto;

import java.util.List;

public record ReservationPreemptiveRequest(
        Long userId,
        List<Long> shopReservationDateTimeSeatIds,
        String requirement,
        int personCount
) {
}