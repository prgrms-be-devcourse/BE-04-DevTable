package com.mdh.owner.global.security.controller;

import com.mdh.owner.global.ApiResponse;
import com.mdh.owner.global.security.session.CurrentUser;
import com.mdh.owner.global.security.LoginService;
import com.mdh.owner.global.security.session.UserInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RequiredArgsConstructor
@RestController
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/api/v1/owner")
    public ResponseEntity<ApiResponse<Void>> signUp(@Valid @RequestBody SignUpRequest request) {
        var ownerId = loginService.signUp(request);

        return ResponseEntity.created(URI.create("/api/v1/owner/" + ownerId))
                .body(ApiResponse.created(null));
    }

    @GetMapping("/api/v1/me")
    public ResponseEntity<ApiResponse<UserInfo>> me(@CurrentUser UserInfo userInfo) {
        return ResponseEntity.ok(ApiResponse.ok(userInfo));
    }
}