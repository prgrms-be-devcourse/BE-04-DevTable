package com.mdh.devtable.ownerreservation.presentation.dto;

import com.mdh.devtable.reservation.domain.ShopReservation;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ShopReservationCreateRequest(

        @NotNull(message = "최대 인원은 필수입니다.")
        @Min(value = 1, message = "최대 인원은 1 이상이어야 합니다.")
        @Max(value = 30, message = "최대 인원은 30 이하이어야 합니다.")
        Integer maximumPeople,

        @NotNull(message = "최소 인원은 필수입니다.")
        @Min(value = 1, message = "최소 인원은 1 이상이어야 합니다.")
        Integer minimumPeople
) {
    public ShopReservation toEntity(Long shopId) {
        return new ShopReservation(shopId, minimumPeople, maximumPeople);
    }
}