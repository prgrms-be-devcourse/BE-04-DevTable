package com.mdh.common.reservation.persistence.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationAlarmInfo(
        Long userId,
        String shopName,
        LocalDate reservationDate,
        LocalTime reservationTime,
        int totalPeople,
        String shopPhoneNumber,
        String shopInfo
) {
}