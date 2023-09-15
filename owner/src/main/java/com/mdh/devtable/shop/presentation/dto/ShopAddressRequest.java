package com.mdh.devtable.shop.presentation.dto;

import com.mdh.devtable.shop.domain.ShopAddress;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ShopAddressRequest(

        @NotBlank(message = "주소는 비어 있을 수 없습니다.")
        @Size(max = 127, message = "주소는 최대 127자까지 가능합니다.")
        String address,

        @NotBlank(message = "우편번호는 비어 있을 수 없습니다.")
        @Size(max = 7, message = "우편번호는 최대 7자까지 가능합니다.")
        String zipcode,

        @NotBlank(message = "위도는 비어 있을 수 없습니다.")
        @Digits(integer = 10, fraction = 6, message = "위도는 숫자만 가능하며, 소수점 아래는 최대 6자리까지 가능합니다.")
        String latitude,

        @NotBlank(message = "경도는 비어 있을 수 없습니다.")
        @Digits(integer = 10, fraction = 6, message = "경도는 숫자만 가능하며, 소수점 아래는 최대 6자리까지 가능합니다.")
        String longitude
) {
    public ShopAddress toVO() {
        return ShopAddress.builder()
                .address(address)
                .zipcode(zipcode)
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }
}