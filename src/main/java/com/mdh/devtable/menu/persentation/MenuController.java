package com.mdh.devtable.menu.persentation;

import com.mdh.devtable.global.ApiResponse;
import com.mdh.devtable.menu.application.MenuService;
import com.mdh.devtable.menu.persentation.dto.MenuCreateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RequiredArgsConstructor
@RestController
public class MenuController {

    private final MenuService menuService;

    @PostMapping("/api/owner/v1/shops/menus")
    public ResponseEntity<ApiResponse<Void>> createMenu(@Valid @RequestBody MenuCreateRequest request) {
        menuService.createMenu(request);
        var uri = URI.create("/api/owner/v1/shops/menus");
        return ResponseEntity.created(uri)
                .body(ApiResponse.created(null));
    }
}