package com.mdh.devtable.ownerreservation.presentation;

import com.mdh.devtable.global.ApiResponse;
import com.mdh.devtable.ownerreservation.application.OwnerReservationService;
import com.mdh.devtable.ownerreservation.presentation.dto.SeatCreateRequest;
import com.mdh.devtable.ownerreservation.presentation.dto.ShopReservationCreateRequest;
import com.mdh.devtable.ownerreservation.presentation.dto.ShopReservationDateTimeCreateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RequestMapping("/api/owner/v1")
@RequiredArgsConstructor
@RestController
public class OwnerReservationController {

    private final OwnerReservationService ownerReservationService;

    @PostMapping("/shops/{shopId}/reservation")
    public ResponseEntity<ApiResponse<Void>> createShopReservation(@PathVariable("shopId") Long shopId, @Valid @RequestBody ShopReservationCreateRequest request) {
        var shopReservationId = ownerReservationService.createShopReservation(shopId, request);
        var uri = URI.create(String.format("/api/owner/v1/shops/%d/reservation/%d", shopId, shopReservationId));

        return ResponseEntity.created(uri)
                .body(ApiResponse.created(null));
    }

    @PostMapping("/shops/{shopId}/seats")
    public ResponseEntity<ApiResponse<Void>> createSeat(@PathVariable("shopId") Long shopId, @Valid @RequestBody SeatCreateRequest request) {
        var seatId = ownerReservationService.saveSeat(shopId, request);
        var uri = URI.create(String.format("/api/owner/v1/shops/%d/seats/%d", shopId, seatId));

        return ResponseEntity.created(uri)
                .body(ApiResponse.created(null));
    }

    @PostMapping("/shops/{shopId}/reservation-date-time")
    public ResponseEntity<ApiResponse<Void>> createShopReservationDateTime(@PathVariable("shopId") Long shopId, @Valid @RequestBody ShopReservationDateTimeCreateRequest request) {
        var shopReservationDateTimeId = ownerReservationService.createShopReservationDateTime(shopId, request);
        var uri = URI.create(String.format("/api/owner/v1/shops/%d/reservation-date-time/%d",
                shopId,
                shopReservationDateTimeId));

        return ResponseEntity.created(uri)
                .body(ApiResponse.created(null));
    }

    @PostMapping("/shop-reservation-date-times/{shopReservationDateTimeId}/seats/{seatId}/shop-reservation-date-time-seats")
    public ResponseEntity<ApiResponse<Void>> createShopReservationDateTimeSeat(@PathVariable("shopReservationDateTimeId") Long shopReservationDateTimeId, @PathVariable("seatId") Long seatId) {
        var shopReservationDateTimeSeatId = ownerReservationService.createShopReservationDateTimeSeat(shopReservationDateTimeId, seatId);
        var uri = URI.create(String.format("/api/owner/v1/shop-reservation-date-times/%d/seats/%d/shop-reservation-date-time-seats/%d",
                shopReservationDateTimeId,
                seatId,
                shopReservationDateTimeSeatId));

        return ResponseEntity.created(uri)
                .body(ApiResponse.created(null));
    }
}