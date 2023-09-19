package com.mdh.user.shop.presentation;

import com.mdh.user.global.ApiResponse;
import com.mdh.user.shop.application.ShopService;
import com.mdh.user.shop.application.dto.ShopResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/customer/v1/shops")
public class ShopController {

    private final ShopService shopService;

    @GetMapping("/waitings")
    public ResponseEntity<ApiResponse<ShopResponses>> findByConditionWithWaiting(
            @RequestParam MultiValueMap<String, String> multiParams,
            Pageable pageable) {

        var result = shopService.findByConditionWithWaiting(multiParams, pageable);
        return new ResponseEntity<>(ApiResponse.ok(result), HttpStatus.OK);
    }
}