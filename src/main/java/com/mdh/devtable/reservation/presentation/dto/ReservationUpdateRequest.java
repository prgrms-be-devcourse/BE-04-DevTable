package com.mdh.devtable.reservation.presentation.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record ReservationUpdateRequest(

        @NotEmpty(message = "매장을 예약하기 위한 좌석 Id는 예약 인원수와 동일하게 전달 받아야합니다.")
        List<Long> shopReservationDateTimeSeatsId,

        @Min(value = 1, message = "예약 인원수는 1 미만 일 수 없습니다.")
        @Max(value = 30, message = "예약 인원수는 30 을 초과 할 수 없습니다.")
        int personCount
) {
}
