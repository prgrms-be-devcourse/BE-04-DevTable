package com.mdh.devtable.ownerreservation.presentation.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record ShopReservationDateTimeCreateRequest(
        LocalDate localDate,
        LocalTime localTime
) {
}