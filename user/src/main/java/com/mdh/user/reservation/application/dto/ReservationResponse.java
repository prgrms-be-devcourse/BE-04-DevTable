package com.mdh.user.reservation.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.mdh.common.reservation.ReservationStatus;
import com.mdh.common.reservation.persistence.dto.ReservationQueryDto;
import com.mdh.common.shop.domain.ShopType;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationResponse(
        ShopDto shop,
        ReservationDto reservation
) {

    public ReservationResponse(ReservationQueryDto reservationQueryDto) {
        this(new ShopDto(reservationQueryDto), new ReservationDto(reservationQueryDto));
    }

    public record ShopDto(Long id,
                          String name,
                          ShopType shopType,
                          String region) {

        public ShopDto(ReservationQueryDto reservationQueryDto) {
            this(reservationQueryDto.shopId(),
                    reservationQueryDto.name(),
                    reservationQueryDto.shopType(),
                    fieldToRegion(reservationQueryDto.city(), reservationQueryDto.district())
            );
        }

        private static String fieldToRegion(String city, String district) {
            StringBuilder sb = new StringBuilder();
            sb.append(city)
                    .append(" ")
                    .append(district);

            return sb.toString();
        }
    }

    public record ReservationDto(
            @JsonSerialize(using = LocalDateSerializer.class)
            @JsonDeserialize(using = LocalDateDeserializer.class)
            @JsonFormat(pattern = "yyyy-MM-dd")
            LocalDate reservationDate,

            @JsonSerialize(using = LocalTimeSerializer.class)
            @JsonDeserialize(using = LocalTimeDeserializer.class)
            @JsonFormat(pattern = "HH:mm:ss")
            LocalTime reservationTime,

            int personCount,

            ReservationStatus reservationStatus
    ) {
        public ReservationDto(ReservationQueryDto reservationQueryDto) {
            this(
                    reservationQueryDto.reservationDate(),
                    reservationQueryDto.reservationTime(),
                    reservationQueryDto.personCount(),
                    reservationQueryDto.reservationStatus()
            );
        }
    }
}


