package com.mdh.owner.reservation.write.presentation.dto;

import com.mdh.common.reservation.domain.SeatType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SeatCreateRequest(

        @NotNull(message = "좌석 타입을 입력해 주세요.")
        SeatType seatType,

        @Max(value = 99999, message = "99999 이하의 값을 입력해 주세요")
        @Min(value = 1, message = "1 이상의 값을 입력해 주세요.")
        @NotNull(message = "좌석(테이블)의 인원 수를 설정해 주세요.")
        Integer count
) {
}