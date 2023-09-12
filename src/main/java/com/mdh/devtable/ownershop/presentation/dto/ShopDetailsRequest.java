package com.mdh.devtable.ownershop.presentation.dto;

import com.mdh.devtable.shop.ShopDetails;

public record ShopDetailsRequest(
        String introduce,
        String openingHours,
        String info,
        String url,
        String phoneNumber,
        String holiday
) {
    public ShopDetails toVO() {
        return ShopDetails.builder()
                .introduce(introduce)
                .openingHours(openingHours)
                .info(info)
                .url(url)
                .phoneNumber(phoneNumber)
                .holiday(holiday)
                .build();
    }
}