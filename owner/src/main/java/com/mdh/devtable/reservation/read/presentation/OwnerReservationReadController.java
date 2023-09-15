package com.mdh.devtable.reservation.read.presentation;

import com.mdh.devtable.global.ApiResponse;
import com.mdh.devtable.reservation.ReservationStatus;
import com.mdh.devtable.reservation.persistence.dto.OwnerShopReservationInfoResponse;
import com.mdh.devtable.reservation.read.application.OwnerReservationReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/owner/v1")
@RequiredArgsConstructor
@RestController
public class OwnerReservationReadController {

    private final OwnerReservationReadService ownerReservationReadService;

    @GetMapping("/shops/{ownerId}/reservations")
    public ResponseEntity<ApiResponse<List<OwnerShopReservationInfoResponse>>> findAllReservationsByOwnerIdAndStatus(@PathVariable("ownerId") Long ownerId, @RequestParam("status") ReservationStatus status) {
        var response = ownerReservationReadService.findAllReservationsByOwnerIdAndStatus(ownerId, status);

        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}