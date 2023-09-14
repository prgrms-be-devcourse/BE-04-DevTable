package com.mdh.devtable.reservation.controller.dto;

import java.util.List;

public record ReservationCancelRequest(
        List<Long> shopReservationDateTimeSeatIds
) {
}
