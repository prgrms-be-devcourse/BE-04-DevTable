package com.mdh.devtable.ownershop.presentation;

import com.mdh.devtable.global.ApiResponse;
import com.mdh.devtable.ownershop.application.OwnerShopService;
import com.mdh.devtable.ownershop.presentation.dto.OwnerShopCreateRequest;
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
public class OwnerShopController {

    private final OwnerShopService ownerShopService;

    @PostMapping("/api/owner/v1/shops/{ownerId}")
    public ResponseEntity<ApiResponse<Void>> createShop(@PathVariable("ownerId") Long ownerId, @Valid @RequestBody OwnerShopCreateRequest request) {
        var shopId = ownerShopService.createShop(ownerId, request);
        var uri = URI.create(String.format("/api/owner/v1/shops/%d/%d", ownerId, shopId));
        return ResponseEntity.created(uri)
                .body(ApiResponse.created(null));
    }

}