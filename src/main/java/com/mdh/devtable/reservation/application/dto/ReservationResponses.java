package com.mdh.devtable.reservation.application.dto;

import java.util.List;

public record ReservationResponses(
        List<ReservationResponse> reservations
) {
}
