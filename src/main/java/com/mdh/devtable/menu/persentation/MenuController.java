package com.mdh.devtable.menu.persentation;

import com.mdh.devtable.global.ApiResponse;
import com.mdh.devtable.menu.application.MenuService;
import com.mdh.devtable.menu.persentation.dto.MenuCreateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MenuController {

    private final MenuService menuService;

    @PostMapping("/api/owner/v1/shops/menus")
    public ResponseEntity<ApiResponse<Void>> createMenu(@Valid @RequestBody MenuCreateRequest request) {
        menuService.createMenu(request);
        return new ResponseEntity<>(ApiResponse.created(null), HttpStatus.CREATED);
    }
}