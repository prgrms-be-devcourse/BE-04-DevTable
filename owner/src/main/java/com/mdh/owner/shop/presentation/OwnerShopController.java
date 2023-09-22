package com.mdh.owner.shop.presentation;

import com.mdh.owner.global.ApiResponse;
import com.mdh.owner.global.security.session.CurrentUser;
import com.mdh.owner.global.security.session.UserInfo;
import com.mdh.owner.shop.application.OwnerShopService;
import com.mdh.owner.shop.application.dto.ShopDetailInfoResponse;
import com.mdh.owner.shop.presentation.dto.OwnerShopCreateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/owner/v1/shop")
public class OwnerShopController {

    private final OwnerShopService ownerShopService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createShop(@CurrentUser UserInfo userInfo, @Valid @RequestBody OwnerShopCreateRequest request) {
        var shopId = ownerShopService.createShop(userInfo.userId(), request);
        var uri = URI.create(String.format("/api/owner/v1/shops/%d", shopId));
        return ResponseEntity.created(uri)
                .body(ApiResponse.created(null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ShopDetailInfoResponse>> findShopByOwner(@CurrentUser UserInfo userInfo) {
        var shopDetails = ownerShopService.findShopByOwner(userInfo.userId());
        return ResponseEntity.ok(ApiResponse.ok(shopDetails));
    }

}