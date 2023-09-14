package com.mdh.devtable.reservation.presentation.controller;

import com.mdh.devtable.global.ApiResponse;
import com.mdh.devtable.reservation.application.ReservationService;
import com.mdh.devtable.reservation.application.dto.ReservationResponses;
import com.mdh.devtable.reservation.domain.ReservationStatus;
import com.mdh.devtable.reservation.presentation.dto.ReservationUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/customer/v1/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @PatchMapping("/{reservationId}/cancel")
    public ResponseEntity<ApiResponse<String>> cancelReservation(@PathVariable Long reservationId) {

        var result = reservationService.cancelReservation(reservationId);
        return new ResponseEntity<>(ApiResponse.ok(result), HttpStatus.OK);
    }

    @PatchMapping("/{reservationId}")
    public ResponseEntity<ApiResponse<Void>> updateReservation(
            @PathVariable Long reservationId,
            @RequestBody @Valid ReservationUpdateRequest request
    ) {
        reservationService.updateReservation(reservationId, request);
        return new ResponseEntity<>(ApiResponse.ok(null), HttpStatus.OK);
    }

    @GetMapping("/me/{userId}")
    public ResponseEntity<ApiResponse<ReservationResponses>> findReservations(
            @PathVariable Long userId,
            @RequestParam("status") ReservationStatus status
    ) {
        var reservationResponses = reservationService.findAllReservations(userId, status);
        return new ResponseEntity<>(ApiResponse.ok(reservationResponses), HttpStatus.OK);
    }
}
