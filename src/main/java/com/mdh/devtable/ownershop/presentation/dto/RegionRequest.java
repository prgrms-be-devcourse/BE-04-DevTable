package com.mdh.devtable.ownershop.presentation.dto;

import com.mdh.devtable.shop.Region;

public record RegionRequest(
        String city,
        String district
) {
    public Region toEntity() {
        return Region.builder()
                .city(city)
                .district(district)
                .build();
    }
}