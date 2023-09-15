package com.mdh.user.reservation.presentation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ReservationCancelRequest(

        @NotNull(message = "선점을 취소할 예약 좌석은 null이면 안됩니다.")
        @Size(min = 1, message = "선점을 취소할 예약 좌석은 1개 이상이어야 합니다.")
        List<Long> shopReservationDateTimeSeatIds
) {
}
