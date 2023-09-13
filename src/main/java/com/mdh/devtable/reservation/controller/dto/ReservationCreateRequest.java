package com.mdh.devtable.reservation.controller.dto;

import java.util.List;

public record ReservationCreateRequest(
        Long userId,
        Long shopId,
        List<Long> shopReservationDateTimeSeatIds,
        String requirement,
        int person_count
) {
}