package com.mdh.user.controller;

import com.mdh.user.global.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestApiController {

    @GetMapping("/hello")
    public ApiResponse<String> hello() {
        return ApiResponse.ok("hello");
    }
}
