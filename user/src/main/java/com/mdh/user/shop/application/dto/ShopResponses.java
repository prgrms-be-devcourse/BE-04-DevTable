package com.mdh.user.shop.application.dto;

import org.springframework.data.domain.Page;

public record ShopResponses(
        Page<ShopResponse> shopResponses
) {
}
