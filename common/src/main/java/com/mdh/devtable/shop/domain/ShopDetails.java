package com.mdh.devtable.shop.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class ShopDetails {

    @Lob
    @Column(name = "introduce", nullable = false)
    private String introduce;

    @Column(name = "opening_hours", length = 127, nullable = false)
    private String openingHours;

    @Lob
    @Column(name = "info", nullable = true)
    private String info;

    @Column(name = "url", length = 127, nullable = true)
    private String url;

    @Column(name = "phone_number", length = 31, nullable = true)
    private String phoneNumber;

    @Column(name = "holiday", length = 127, nullable = true)
    private String holiday;

    @Builder
    public ShopDetails(
            @NonNull String introduce,
            @NonNull String openingHours,
            String info,
            String url,
            String phoneNumber,
            String holiday
    ) {
        this.introduce = introduce;
        this.openingHours = openingHours;
        this.info = info;
        this.url = url;
        this.phoneNumber = phoneNumber;
        this.holiday = holiday;
    }
}