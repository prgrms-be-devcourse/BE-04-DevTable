package com.mdh.devtable.ownerreservation.read.application.dto;

import com.mdh.devtable.reservation.domain.ReservationStatus;
import com.mdh.devtable.reservation.domain.SeatType;

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