package com.mdh.owner.waiting.presentation.controller;


import com.mdh.common.waiting.domain.WaitingStatus;
import com.mdh.common.waiting.persistence.dto.WaitingInfoResponseForOwner;
import com.mdh.owner.global.ApiResponse;
import com.mdh.owner.global.security.session.CurrentUser;
import com.mdh.owner.global.security.session.UserInfo;
import com.mdh.owner.waiting.application.OwnerWaitingService;
import com.mdh.owner.waiting.presentation.dto.OwnerShopWaitingStatusChangeRequest;
import com.mdh.owner.waiting.presentation.dto.OwnerUpdateShopWaitingInfoRequest;
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
    @PatchMapping("/waitings/shops/{shopId}")
    public ResponseEntity<ApiResponse<Void>> changShopWaitingStatus(@RequestBody OwnerShopWaitingStatusChangeRequest request, @PathVariable("shopId") Long shopId) {
        ownerWaitingService.changeShopWaitingStatus(shopId, request);
        return new ResponseEntity<>(ApiResponse.ok(null), HttpStatus.OK);
    }

    @PatchMapping("/waitings/{waitingId}/visit")
    public ResponseEntity<ApiResponse<Void>> markWaitingStatusAsVisited(@PathVariable("waitingId") Long waitingId) {
        ownerWaitingService.markWaitingStatusAsVisited(waitingId);
        return new ResponseEntity<>(ApiResponse.ok(null), HttpStatus.OK);
    }

    @PatchMapping("/waitings/{waitingId}/cancel")
    public ResponseEntity<ApiResponse<Void>> markWaitingStatusAsCancel(@PathVariable("waitingId") Long waitingId) {
        ownerWaitingService.markWaitingStatusAsCancel(waitingId);
        return new ResponseEntity<>(ApiResponse.ok(null), HttpStatus.OK);
    }

    @PatchMapping("/waitings/{waitingId}/no-show")
    public ResponseEntity<ApiResponse<Void>> markWaitingStatusAsNoShow(@PathVariable("waitingId") Long waitingId) {
        ownerWaitingService.markWaitingStatusAsNoShow(waitingId);
        return new ResponseEntity<>(ApiResponse.ok(null), HttpStatus.OK);
    }

    @GetMapping("/waitings")
    public ResponseEntity<ApiResponse<List<WaitingInfoResponseForOwner>>> findWaitingByOwnerIdAndWaitingStatus(@RequestParam("status") WaitingStatus status, @CurrentUser UserInfo userInfo) {
        var body = ownerWaitingService.findWaitingOwnerIdAndWaitingStatus(userInfo.userId(), status);
        return new ResponseEntity<>(ApiResponse.ok(body), HttpStatus.OK);
    }

    @PatchMapping("/shops/{shopId}/waiting")
    public ResponseEntity<ApiResponse<Void>> updateShopWaitingInfo(@PathVariable("shopId") Long shopId, @RequestBody @Valid OwnerUpdateShopWaitingInfoRequest request) {
        ownerWaitingService.updateShopWaitingInfo(shopId, request);
        return new ResponseEntity<>(ApiResponse.ok(null), HttpStatus.OK);
    }
}