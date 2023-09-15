package com.mdh.user.waiting.application.dto;


import com.mdh.common.shop.domain.ShopType;
import com.mdh.common.waiting.persistence.dto.WaitingDetailsQueryDto;

public record ShopWaitingResponse(
        String shopName,
        ShopType shopType,
        String region,
        ShopDetailsDto shopDetails
) {

    public ShopWaitingResponse(WaitingDetailsQueryDto dto) {
        this(dto.shopName(), dto.shopType(), dto.region(), new ShopDetailsDto(dto));
    }

    public record ShopDetailsDto(
            String introduce,
            String openingHours,
            String info,
            String url,
            String phoneNumber,
            String holiday
    ) {
        public ShopDetailsDto(WaitingDetailsQueryDto dto) {
            this(
                    dto.shopDetails().getIntroduce(),
                    dto.shopDetails().getOpeningHours(),
                    dto.shopDetails().getInfo(),
                    dto.shopDetails().getUrl(),
                    dto.shopDetails().getPhoneNumber(),
                    dto.shopDetails().getHoliday()
            );
        }
    }
}
