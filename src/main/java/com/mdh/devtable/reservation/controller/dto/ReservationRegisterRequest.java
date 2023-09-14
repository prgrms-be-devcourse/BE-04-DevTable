package com.mdh.devtable.reservation.controller.dto;

import java.util.List;

public record ReservationRegisterRequest(
        Long shopId,
        List<Long> shopReservationDateTimeSeatIds,
        int totalSeatCount
) {
}
