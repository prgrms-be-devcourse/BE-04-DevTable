package com.mdh.user.reservation.application.dto;

import java.util.List;

public record ReservationResponses(
        List<ReservationResponse> reservations
) {
}
