package com.mdh.user.shop.application.dto;

import com.mdh.common.shop.persistence.dto.ReservationShopSearchQueryDto;
import org.springframework.data.domain.Page;

import java.util.List;

public record ReservationShopSearchResponse(
        int totalPages,
        boolean hasNext,
        List<ReservationShopSearchQueryDto> shops
) {
    public static ReservationShopSearchResponse of(Page<ReservationShopSearchQueryDto> page) {
        return new ReservationShopSearchResponse(
                page.getTotalPages(),
                page.hasNext(),
                page.getContent()
        );
    }
}