package com.mdh.user.shop.application.dto;

import com.mdh.common.shop.domain.Shop;
import com.mdh.common.shop.domain.ShopAddress;
import com.mdh.common.shop.domain.ShopDetails;
import com.mdh.common.shop.domain.ShopType;

public record ShopDetailInfoResponse(
        String name,
        String description,
        ShopType shopType,
        ShopDetailsResponse shopDetails,
        ShopPriceResponse shopPrice,
        ShopAddressResponse shopAddress,
        ShopRegionResponse shopRegion
) {
    // In ShopDetailInfoResponse.java
    public static ShopDetailInfoResponse from(Shop shop) {
        return new ShopDetailInfoResponse(
                shop.getName(),
                shop.getDescription(),
                shop.getShopType(),
                new ShopDetailsResponse(
                        shop.getShopDetails().getIntroduce(),
                        shop.getShopDetails().getOpeningHours(),
                        shop.getShopDetails().getInfo(),
                        shop.getShopDetails().getUrl(),
                        shop.getShopDetails().getPhoneNumber(),
                        shop.getShopDetails().getHoliday()
                ),
                new ShopPriceResponse(
                        shop.getShopPrice().getLunchMinPrice(),
                        shop.getShopPrice().getLunchMaxPrice(),
                        shop.getShopPrice().getDinnerMinPrice(),
                        shop.getShopPrice().getDinnerMaxPrice()
                ),
                new ShopAddressResponse(
                        shop.getShopAddress().getZipcode(),
                        shop.getShopAddress().getAddress(),
                        shop.getShopAddress().getLatitude(),
                        shop.getShopAddress().getLongitude()
                ),
                new ShopRegionResponse(
                        shop.getRegion().getCity(),
                        shop.getRegion().getDistrict()
                )
        );
    }

    public record ShopDetailsResponse(
            String introduce,
            String openingHour,
            String info,
            String url,
            String phoneNumber,
            String holiday
    ) {}

    public record ShopPriceResponse(
            Integer lunchMinPrice,
            Integer lunchMaxPrice,
            Integer DinnerMinPrice,
            Integer DinnerMaxPrice

    ) {}

    public record ShopAddressResponse(
            String zipcode,
            String address,
            String latitude,
            String longitude
    ) {

    }

    public record ShopRegionResponse(
            String city,
            String region
    ) {

    }
}