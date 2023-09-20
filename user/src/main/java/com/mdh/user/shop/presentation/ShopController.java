package com.mdh.user.shop.presentation;

import com.mdh.user.global.ApiResponse;
import com.mdh.user.shop.application.ShopService;
import com.mdh.user.shop.application.dto.ReservationShopSearchResponse;
import com.mdh.user.shop.application.dto.ShopDetailInfoResponse;
import com.mdh.user.shop.application.dto.ShopResponses;
import com.mdh.user.shop.presentation.dto.ReservationShopSearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/customer/v1/shops")
public class ShopController {

    private final ShopService shopService;

    @GetMapping("/waitings")
    public ResponseEntity<ApiResponse<ShopResponses>> findByConditionWithWaiting(
            @RequestParam MultiValueMap<String, String> multiParams,
            Pageable pageable) {

        var result = shopService.findByConditionWithWaiting(multiParams, pageable);
        return new ResponseEntity<>(ApiResponse.ok(result), HttpStatus.OK);
    }

    @GetMapping("/reservations")
    public ResponseEntity<ApiResponse<ReservationShopSearchResponse>> searchReservationShops(
            @PageableDefault(sort = "createdDate", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam Map<String, String> params) {
        var reservationShopSearchResponse = shopService.searchReservationShop(pageable, ReservationShopSearchRequest.of(params));
        return ResponseEntity.ok(ApiResponse.ok(reservationShopSearchResponse));
    }

    @GetMapping("/{shopId}")
    public ResponseEntity<ApiResponse<ShopDetailInfoResponse>> findShopDetailsById(@PathVariable("shopId") Long shopId) {
        var result = shopService.findShopDetailsById(shopId);

        return ResponseEntity.ok(ApiResponse.ok(result));
    }
}