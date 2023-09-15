package com.mdh.user.reservation.presentation.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record ReservationUpdateRequest(

        @NotEmpty(message = "변경하고자 하는 예약의 좌석이 비어있을 수 없습니다.")
        List<Long> shopReservationDateTimeSeatsIds,

        @Min(value = 1, message = "예약 인원수는 1 미만 일 수 없습니다.")
        @Max(value = 30, message = "예약 인원수는 30 을 초과 할 수 없습니다.")
        int personCount
) {
}
