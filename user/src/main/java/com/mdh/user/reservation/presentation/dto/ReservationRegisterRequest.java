package com.mdh.user.reservation.presentation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ReservationRegisterRequest(

        @NotNull(message = "예약하려는 매장의 아이디는 null이면 안됩니다.")
        Long shopId,

        @NotNull(message = "예약 확정하려는 좌석이 null이면 안됩니다.")
        @Size(min = 1, message = "예약 확정하려는 좌석은 1개 이상이어야 합니다.")
        List<Long> shopReservationDateTimeSeatIds,

        @Min(value = 0, message = "예약하려는 좌석의 전체 수는 0이상이어야 합니다.")
        int totalSeatCount
) {
}
