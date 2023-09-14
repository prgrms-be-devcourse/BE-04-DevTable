package com.mdh.devtable.ownershop.presentation.dto;

import com.mdh.devtable.global.util.RegularExpression;
import com.mdh.devtable.shop.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record OwnerShopCreateRequest(

        @NotBlank(message = "상점 이름은 필수입니다.")
        String name,

        @NotBlank(message = "상점 설명은 필수입니다.")
        String description,

        @NotNull(message = "상점 유형은 필수입니다.")
        ShopType shopType,

        @Valid
        ShopDetailsRequest shopDetailsRequest,

        @Valid
        ShopAddressRequest shopAddressRequest,

        @Valid
        RegionRequest regionRequest

) {
    public static record RegionRequest(

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

    public static record ShopAddressRequest(

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

    public static record ShopDetailsRequest(

            @NotBlank(message = "소개는 비어 있을 수 없습니다.")
            String introduce,

            @NotBlank(message = "영업 시간은 비어 있을 수 없습니다.")
            @Size(max = 127, message = "영업 시간은 최대 127자까지 가능합니다.")
            String openingHours,

            String info,

            @Pattern(regexp = RegularExpression.URL, message = "유효한 URL 형식이어야 합니다.")
            @Size(max = 127, message = "URL은 최대 127자까지 가능합니다.")
            String url,

            @Pattern(regexp = RegularExpression.SHOP_TEL_NUMBER, message = "전화번호는 10~11자리 숫자만 가능합니다.")
            String phoneNumber,

            @Size(max = 127, message = "휴일 정보는 최대 127자까지 가능합니다.")
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

    public Shop toEntity(Long userId) {
        return Shop.builder()
                .userId(userId)
                .name(name)
                .description(description)
                .shopDetails(shopDetailsRequest.toVO())
                .shopAddress(shopAddressRequest.toVO())
                .shopType(shopType)
                .region(regionRequest.toEntity())
                .build();
    }
}