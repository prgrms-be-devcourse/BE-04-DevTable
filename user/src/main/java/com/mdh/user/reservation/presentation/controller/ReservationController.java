package com.mdh.user.reservation.presentation.controller;

import com.mdh.common.reservation.domain.ReservationStatus;
import com.mdh.user.global.ApiResponse;
import com.mdh.user.global.security.session.CurrentUser;
import com.mdh.user.global.security.session.UserInfo;
import com.mdh.user.reservation.application.ReservationService;
import com.mdh.user.reservation.application.dto.ReservationResponses;
import com.mdh.user.reservation.presentation.dto.ReservationCancelRequest;
import com.mdh.user.reservation.presentation.dto.ReservationPreemptiveRequest;
import com.mdh.user.reservation.presentation.dto.ReservationRegisterRequest;
import com.mdh.user.reservation.presentation.dto.ReservationUpdateRequest;
import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/customer/v1/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/preemption")
    public ResponseEntity<ApiResponse<UUID>> preemptReservation(@RequestBody @Valid ReservationPreemptiveRequest reservationPreemptiveRequest, @CurrentUser UserInfo userInfo) {
        UUID reservationId = reservationService.preemptiveReservation(userInfo.userId(), reservationPreemptiveRequest);
        return ResponseEntity.ok(ApiResponse.ok(reservationId));
    }

    @PostMapping("/{reservationId}/register")
    public ResponseEntity<ApiResponse<Void>> registerReservation(@PathVariable UUID reservationId,
                                                                 @RequestBody @Valid ReservationRegisterRequest reservationRegisterRequest) {
        Long registeredReservationId = reservationService.registerReservation(reservationId, reservationRegisterRequest);
        return ResponseEntity.created(URI.create(String.format("/api/customer/v1/reservations/%d", registeredReservationId)))
                .body(ApiResponse.created(null));
    }

    @PostMapping("/preemption/{reservationId}/cancel")
    public ResponseEntity<ApiResponse<String>> cancelPreemptiveReservation(@PathVariable UUID reservationId,
                                                                           @RequestBody @Valid ReservationCancelRequest reservationCancelRequest) {
        String message = reservationService.cancelPreemptiveReservation(reservationId, reservationCancelRequest);
        return ResponseEntity.ok(ApiResponse.ok(message));
    }

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

    @Timed("user.reservation.findAll")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<ReservationResponses>> findReservations(
            @CurrentUser UserInfo userInfo,
            @RequestParam("status") ReservationStatus status
    ) {
        var reservationResponses = reservationService.findAllReservations(userInfo.userId(), status);
        return new ResponseEntity<>(ApiResponse.ok(reservationResponses), HttpStatus.OK);
    }
}