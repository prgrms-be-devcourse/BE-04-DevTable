package com.mdh.devtable.ownerreservation.presentation;

import com.mdh.devtable.global.ApiResponse;
import com.mdh.devtable.ownerreservation.application.OwnerReservationService;
import com.mdh.devtable.ownerreservation.presentation.dto.ShopReservationCreateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RequiredArgsConstructor
@RestController
public class OwnerReservationController {

    private final OwnerReservationService ownerReservationService;

    @PostMapping("/api/owner/v1/shops/{shopId}/reservation")
    public ResponseEntity<ApiResponse<Void>> createShopReservation(@PathVariable("shopId") Long shopId, @Valid @RequestBody ShopReservationCreateRequest request) {
        var shopReservationId = ownerReservationService.createShopReservation(shopId, request);
        var uri = URI.create(String.format("/api/owner/v1/shops/%d/reservation/%d", shopId, shopReservationId));
        
        return ResponseEntity.created(uri)
                .body(ApiResponse.created(null));
    }
}