package com.mdh.user.shop.presentation.controller;

import com.mdh.user.global.ApiResponse;
import com.mdh.user.shop.application.ShopService;
import com.mdh.user.shop.application.dto.ReservationShopSearchResponse;
import com.mdh.user.shop.presentation.controller.dto.ReservationShopSearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customer/v1/shops")
public class ShopController {

    private final ShopService shopService;

    @GetMapping("/reservations")
    public ResponseEntity<ApiResponse<ReservationShopSearchResponse>> searchReservationShops(
            @PageableDefault(sort = "createdDate", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam Map<String, String> params) {
        var reservationShopSearchResponse = shopService.searchReservationShop(pageable, ReservationShopSearchRequest.of(params));
        return ResponseEntity.ok(ApiResponse.ok(reservationShopSearchResponse));
    }
}