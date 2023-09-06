package com.mdh.devtable.waiting.presentation;

import com.mdh.devtable.global.ApiResponse;
import com.mdh.devtable.waiting.application.WaitingService;
import com.mdh.devtable.waiting.presentation.dto.WaitingCreateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/customer/v1/waitings")
@RequiredArgsConstructor
public class UserWaitingController {

    private final WaitingService waitingService;

    @PostMapping
    public ResponseEntity<ApiResponse<URI>> createWaiting(@RequestBody @Valid WaitingCreateRequest waitingCreateRequest) {
        Long waitingId = waitingService.createWaiting(waitingCreateRequest);
        var createdResponse = ApiResponse.created(URI.create("/api/customer/v1/waitings" + waitingId));
        return new ResponseEntity<>(createdResponse, HttpStatus.CREATED);
    }
}
