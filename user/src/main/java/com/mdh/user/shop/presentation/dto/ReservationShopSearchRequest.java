package com.mdh.user.shop.presentation.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

public record ReservationShopSearchRequest(

        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonDeserialize(using = LocalDateDeserializer.class)
        LocalDate reservationDate,

        @JsonSerialize(using = LocalTimeSerializer.class)
        @JsonDeserialize(using = LocalTimeDeserializer.class)
        LocalTime reservationTime,
        Integer personCount,
        String region,
        Integer minPrice,
        Integer maxPrice
) {
    public static ReservationShopSearchRequest of(Map<String, String> param) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(param, ReservationShopSearchRequest.class);
    }
}