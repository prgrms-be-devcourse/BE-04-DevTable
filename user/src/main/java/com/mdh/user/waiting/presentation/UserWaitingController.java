package com.mdh.user.waiting.presentation;

import com.mdh.common.waiting.domain.WaitingStatus;
import com.mdh.user.global.ApiResponse;
import com.mdh.user.global.security.session.CurrentUser;
import com.mdh.user.global.security.session.UserInfo;
import com.mdh.user.waiting.application.WaitingService;
import com.mdh.user.waiting.application.dto.UserWaitingResponse;
import com.mdh.user.waiting.application.dto.WaitingDetailsResponse;
import com.mdh.user.waiting.presentation.dto.WaitingCreateRequest;
import io.micrometer.core.annotation.Timed;
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

    @Timed("user.waiting.findAllByStatus")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<UserWaitingResponse>>> findWaitingsByUserIdAndStatus(@RequestParam("status") WaitingStatus status, @CurrentUser UserInfo userInfo) {
        var findUserWaitings = waitingService.findAllByUserIdAndStatus(userInfo.userId(), status);
        return ResponseEntity.ok(ApiResponse.ok(findUserWaitings));
    }

    @PostMapping("/shops/{shopId}")
    public ResponseEntity<ApiResponse<URI>> createWaiting(@RequestBody @Valid WaitingCreateRequest waitingCreateRequest, @CurrentUser UserInfo userInfo, @PathVariable("shopId") Long shopId) {
        Long waitingId = waitingService.createWaiting(userInfo.userId(), shopId, waitingCreateRequest);
        return ResponseEntity.created(URI.create(String.format("/api/customer/v1/waitings/%d", waitingId)))
                .body(ApiResponse.created(null));
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

    @PatchMapping("/{waitingId}/postpone")
    public ResponseEntity<ApiResponse<Void>> postponeWaiting(@PathVariable Long waitingId) {
        waitingService.postPoneWaiting(waitingId);
        return new ResponseEntity<>(ApiResponse.ok(null), HttpStatus.OK);
    }
}