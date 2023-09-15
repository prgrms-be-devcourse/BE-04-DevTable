package com.mdh.user.reservation.presentation.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ReservationPreemptiveRequest(

        @NotNull(message = "유저 아이디는 null이면 안됩니다.")
        Long userId,

        @NotNull(message = "선점하려는 좌석이 null이면 안됩니다.")
        @Size(min = 1, message = "선점하려는 좌석은 1개 이상이어야 합니다.")
        List<Long> shopReservationDateTimeSeatIds,

        String requirement,

        @Min(value = 1, message = "예약 인원은 1명 이상이어야 합니다.")
        @Max(value = 30, message = "예약 인원은 30명 이하여야 합니다.")
        int personCount
) {
}