package com.mdh.devtable.ownerreservation.presentation.dto;

import com.mdh.devtable.reservation.domain.SeatType;
import jakarta.validation.constraints.NotNull;

public record SeatCreateRequest(

        @NotNull(message = "좌석 타입을 입력해 주세요.")
        SeatType seatType
) {
}