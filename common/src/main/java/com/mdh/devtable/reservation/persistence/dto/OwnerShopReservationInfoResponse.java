package com.mdh.devtable.reservation.persistence.dto;


import com.mdh.devtable.reservation.ReservationStatus;
import com.mdh.devtable.reservation.SeatType;

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