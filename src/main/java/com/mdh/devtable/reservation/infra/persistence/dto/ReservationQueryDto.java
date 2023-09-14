package com.mdh.devtable.reservation.infra.persistence.dto;

import com.mdh.devtable.reservation.domain.ReservationStatus;
import com.mdh.devtable.shop.ShopType;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationQueryDto(
        Long shopId,
        String name,
        ShopType shopType,
        String city,
        String district,
        LocalDate reservationDate,
        LocalTime reservationTime,
        int personCount,
        ReservationStatus reservationStatus
) {


}
