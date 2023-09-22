package com.mdh.owner.waiting.presentation.controller;

import com.mdh.common.waiting.domain.WaitingStatus;
import com.mdh.common.waiting.persistence.dto.WaitingInfoResponseForOwner;
import com.mdh.owner.global.ApiResponse;
import com.mdh.owner.waiting.application.OwnerWaitingService;
import com.mdh.owner.waiting.presentation.dto.OwnerShopWaitingStatusChangeRequest;
import com.mdh.owner.waiting.presentation.dto.OwnerUpdateShopWaitingInfoRequest;
import com.mdh.owner.waiting.presentation.dto.OwnerWaitingStatusChangeRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/owner/v1")
public class OwnerWaitingController {

    private final OwnerWaitingService ownerWaitingService;

    // TODO Method 명 변경
    @PatchMapping("/shops/{shopId}")
    public ResponseEntity<ApiResponse<Void>> changShopWaitingStatus(@RequestBody OwnerShopWaitingStatusChangeRequest request, @PathVariable("shopId") Long shopId) {
        ownerWaitingService.changeShopWaitingStatus(shopId, request);
        return new ResponseEntity<>(ApiResponse.ok(null), HttpStatus.OK);
    }

    @PatchMapping("/waitings/{waitingId}")
    public ResponseEntity<ApiResponse<Void>> changeWaitingStatus(@RequestBody OwnerWaitingStatusChangeRequest request, @PathVariable("waitingId") Long waitingId) {
        ownerWaitingService.changeWaitingStatus(waitingId, request);
        return new ResponseEntity<>(ApiResponse.ok(null), HttpStatus.OK);
    }

    @GetMapping("/waitings/{ownerId}")
    public ResponseEntity<ApiResponse<List<WaitingInfoResponseForOwner>>> findWaitingByOwnerIdAndWaitingStatus(@RequestParam("status") WaitingStatus status, @PathVariable("ownerId") Long ownerId) {
        var body = ownerWaitingService.findWaitingOwnerIdAndWaitingStatus(ownerId, status);
        return new ResponseEntity<>(ApiResponse.ok(body), HttpStatus.OK);
    }

    @PatchMapping("/shops/{shopId}/waiting")
    public ResponseEntity<ApiResponse<Void>> updateShopWaitingInfo(@PathVariable("shopId") Long shopId, @RequestBody @Valid OwnerUpdateShopWaitingInfoRequest request) {
        ownerWaitingService.updateShopWaitingInfo(shopId, request);
        return new ResponseEntity<>(ApiResponse.ok(null), HttpStatus.OK);
    }
}