package com.mdh.common.shop.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class ShopAddress {

    @Column(name = "address", length = 127, nullable = false)
    private String address;

    @Column(name = "zipcode", length = 7, nullable = false)
    private String zipcode;

    @Column(name = "latitude", length = 31, nullable = false)
    private String latitude;

    @Column(name = "longitude", length = 31, nullable = false)
    private String longitude;

    @Builder
    public ShopAddress(
            @NonNull String address,
            @NonNull String zipcode,
            @NonNull String latitude,
            @NonNull String longitude
    ) {
        this.address = address;
        this.zipcode = zipcode;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}