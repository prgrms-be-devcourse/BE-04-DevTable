package com.mdh.owner.shop.presentation.dto;

import com.mdh.common.shop.domain.Region;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegionRequest(

        @NotBlank(message = "도시 이름은 비어 있을 수 없습니다.")
        @Size(max = 31, message = "도시 이름은 최대 31자까지 가능합니다.")
        String city,

        @NotBlank(message = "지역 이름은 비어 있을 수 없습니다.")
        @Size(max = 31, message = "지역 이름은 최대 31자까지 가능합니다.")
        String district
) {
    public Region toEntity() {
        return Region.builder()
                .city(city)
                .district(district)
                .build();
    }
}