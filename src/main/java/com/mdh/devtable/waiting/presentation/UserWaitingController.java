package com.mdh.devtable.waiting.presentation;

import com.mdh.devtable.global.ApiResponse;
import com.mdh.devtable.waiting.application.WaitingService;
import com.mdh.devtable.waiting.application.dto.UserWaitingResponse;
import com.mdh.devtable.waiting.application.dto.WaitingDetailsResponse;
import com.mdh.devtable.waiting.presentation.dto.MyWaitingsRequest;
import com.mdh.devtable.waiting.presentation.dto.WaitingCreateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/customer/v1/waitings")
@RequiredArgsConstructor
public class UserWaitingController {

    private final WaitingService waitingService;

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<List<UserWaitingResponse>>> findWaitingsByUserIdAndStatus(@RequestBody @Valid MyWaitingsRequest request) {
        var findUserWaitings = waitingService.findAllByUserIdAndStatus(request);
        return ResponseEntity.ok(ApiResponse.ok(findUserWaitings));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<URI>> createWaiting(@RequestBody @Valid WaitingCreateRequest waitingCreateRequest) {
        Long waitingId = waitingService.createWaiting(waitingCreateRequest);
        var createdResponse = ApiResponse.created(URI.create("/api/customer/v1/waitings" + waitingId));
        return new ResponseEntity<>(createdResponse, HttpStatus.CREATED);
    }

    @PatchMapping("/{waitingId}")
    public ResponseEntity<ApiResponse<Void>> cancelWaiting(@PathVariable Long waitingId) {
        waitingService.cancelWaiting(waitingId);
        return new ResponseEntity<>(ApiResponse.noContent(null), HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{waitingId}")
    public ResponseEntity<ApiResponse<WaitingDetailsResponse>> findWaitingDetails(@PathVariable Long waitingId) {
        var waitingDetailsResponse = waitingService.findWaitingDetails(waitingId);
        return new ResponseEntity<>(ApiResponse.ok(waitingDetailsResponse), HttpStatus.OK);
    }
}
