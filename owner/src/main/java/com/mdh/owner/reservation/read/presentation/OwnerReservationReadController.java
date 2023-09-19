package com.mdh.owner.reservation.read.presentation;

import com.mdh.owner.global.ApiResponse;
import com.mdh.common.reservation.domain.ReservationStatus;
import com.mdh.common.reservation.persistence.dto.OwnerShopReservationInfoResponse;
import com.mdh.owner.global.security.CurrentUser;
import com.mdh.owner.global.security.UserInfo;
import com.mdh.owner.reservation.read.application.OwnerReservationReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/owner/v1")
@RequiredArgsConstructor
@RestController
public class OwnerReservationReadController {

    private final OwnerReservationReadService ownerReservationReadService;

    @GetMapping("/shops/reservations")
    public ResponseEntity<ApiResponse<List<OwnerShopReservationInfoResponse>>> findAllReservationsByOwnerIdAndStatus(@CurrentUser UserInfo userInfo, @RequestParam("status") ReservationStatus status) {
        var response = ownerReservationReadService.findAllReservationsByOwnerIdAndStatus(userInfo.userId(), status);

        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}