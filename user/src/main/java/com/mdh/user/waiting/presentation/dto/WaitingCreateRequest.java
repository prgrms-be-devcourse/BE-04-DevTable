package com.mdh.user.waiting.presentation.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record WaitingCreateRequest(

        @Min(value = 0, message = "어른 인원은 0 미만일 수 없습니다.")
        @Max(value = 30, message = "어른 인원은 30 초과일 수 없습니다.")
        int adultCount,

        @Min(value = 0, message = "유아 인원은 0 미만일 수 없습니다.")
        @Max(value = 30, message = "유아 인원은 30 초과일 수 없습니다.")
        int childCount
) {
}