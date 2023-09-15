package com.mdh.common.shop.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "regions")
@Entity
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "city", length = 31, nullable = false)
    private String city;

    @Column(name = "district", length = 31, nullable = false)
    private String district;

    @Builder
    public Region(String city, String district) {
        this.city = city;
        this.district = district;
    }
}