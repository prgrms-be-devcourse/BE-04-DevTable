package com.mdh.devtable.shop.presentation.dto;

import com.mdh.devtable.global.util.RegularExpression;
import com.mdh.devtable.shop.domain.ShopDetails;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ShopDetailsRequest(

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