package com.mdh.devtable.ownerreservation.write.presentation;

import com.mdh.devtable.global.ApiResponse;
import com.mdh.devtable.ownerreservation.write.application.OwnerReservationService;
import com.mdh.devtable.ownerreservation.write.presentation.dto.SeatCreateRequest;
import com.mdh.devtable.ownerreservation.write.presentation.dto.ShopReservationCreateRequest;
import com.mdh.devtable.ownerreservation.write.presentation.dto.ShopReservationDateTimeCreateRequest;
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

    @PatchMapping("/reservation/{reservationId}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelReservationByOwner(@PathVariable("reservationId") Long reservationId) {
        ownerReservationService.cancelReservationByOwner(reservationId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PatchMapping("/reservation/{reservationId}/visit")
    public ResponseEntity<ApiResponse<Void>> markReservationAsVisitedByOwner(@PathVariable("reservationId") Long reservationId) {
        ownerReservationService.markReservationAsVisitedByOwner(reservationId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PatchMapping("/reservation/{reservationId}/no-show")
    public ResponseEntity<ApiResponse<Void>> markReservationAsNoShowByOwner(@PathVariable("reservationId") Long reservationId) {
        ownerReservationService.markReservationAsNoShowByOwner(reservationId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}