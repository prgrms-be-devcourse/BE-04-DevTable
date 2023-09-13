package com.mdh.devtable.reservation.presentation.controller;

import com.mdh.devtable.global.ApiResponse;
import com.mdh.devtable.reservation.application.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
