package com.mdh.devtable.user.presentation;

import com.mdh.devtable.global.ApiResponse;
import com.mdh.devtable.user.application.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/api/v1/users")
    public ResponseEntity<ApiResponse<URI>> signUp(@RequestBody @Valid SignUpRequest request) {
        var id = userService.signUp(request);
        return new ResponseEntity<>(ApiResponse.created(URI.create("/api/v1/users/" + id)), HttpStatus.CREATED);
    }
}