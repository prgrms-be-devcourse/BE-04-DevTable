package com.mdh.user.shop.application;

import com.mdh.common.shop.persistence.ShopRepository;
import com.mdh.user.shop.application.dto.ReservationShopSearchResponse;
import com.mdh.user.shop.presentation.controller.dto.ReservationShopSearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;

    @Transactional(readOnly = true)
    public ReservationShopSearchResponse searchReservationShop(Pageable pageable, ReservationShopSearchRequest reservationShopSearchRequest) {
        var reservationDate = reservationShopSearchRequest.reservationDate();
        var reservationTime = reservationShopSearchRequest.reservationTime();
        var personCount = reservationShopSearchRequest.personCount();
        var region = reservationShopSearchRequest.region();
        var minPrice = reservationShopSearchRequest.minPrice();
        var maxPrice = reservationShopSearchRequest.maxPrice();
        var shopSearchQueryDtos = shopRepository.searchReservationShopByFilter(pageable,
                reservationDate,
                reservationTime,
                personCount,
                region,
                minPrice,
                maxPrice);
        return ReservationShopSearchResponse.of(shopSearchQueryDtos);
    }
}