package com.mdh.common.reservation.persistence.dto;


import com.mdh.common.reservation.domain.ReservationStatus;
import com.mdh.common.reservation.domain.SeatType;

import java.time.LocalDate;
import java.time.LocalTime;

public record OwnerShopReservationInfoResponse(
        String requirement,
        LocalDate reservationDate,
        LocalTime reservationTime,
        ReservationStatus reservationStatus,
        int personCount,
        SeatType seatType
) {
}