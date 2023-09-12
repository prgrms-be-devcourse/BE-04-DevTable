package com.mdh.devtable.ownershop.presentation.dto;

import com.mdh.devtable.shop.ShopAddress;

public record ShopAddressRequest(
        String address,
        String zipcode,
        String latitude,
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