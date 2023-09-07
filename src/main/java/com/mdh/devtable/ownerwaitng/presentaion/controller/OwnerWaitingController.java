package com.mdh.devtable.ownerwaitng.presentaion.controller;

import com.mdh.devtable.global.ApiResponse;
import com.mdh.devtable.ownerwaitng.application.OwnerWaitingService;
import com.mdh.devtable.ownerwaitng.presentaion.dto.OwnerShopWaitingStatusChangeRequest;
import com.mdh.devtable.ownerwaitng.presentaion.dto.OwnerWaitingStatusChangeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/owner/v1")
public class OwnerWaitingController {

    private final OwnerWaitingService ownerWaitingService;

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
}