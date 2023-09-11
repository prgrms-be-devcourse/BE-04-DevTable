package com.mdh.devtable.menu.persentation;

import com.mdh.devtable.global.ApiResponse;
import com.mdh.devtable.menu.application.MenuCategoryService;
import com.mdh.devtable.menu.persentation.dto.MenuCategoryCreateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MenuCategoryController {

    private final MenuCategoryService menuCategoryService;

    @PostMapping("/api/owner/v1/shops/{shopId}/categories")
    public ResponseEntity<ApiResponse<Void>> createMenuCategory(@PathVariable("shopId") Long shopId, @Valid @RequestBody MenuCategoryCreateRequest request) {
        menuCategoryService.createMenuCategory(shopId, request);
        return new ResponseEntity<>(ApiResponse.created(null), HttpStatus.CREATED);
    }
}