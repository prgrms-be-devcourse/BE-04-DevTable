package com.mdh.common.reservation.persistence.dto;

import com.mdh.common.reservation.ReservationStatus;
import com.mdh.common.shop.domain.ShopType;

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
