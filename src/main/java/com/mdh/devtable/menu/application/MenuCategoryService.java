package com.mdh.devtable.menu.application;

import com.mdh.devtable.menu.infra.persistence.MenuCategoryRepository;
import com.mdh.devtable.menu.persentation.dto.MenuCategoryCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MenuCategoryService {

    private final MenuCategoryRepository menuCategoryRepository;

    @Transactional
    public void createMenuCategory(Long shopId, MenuCategoryCreateRequest menuCategoryCreateRequest) {
        menuCategoryRepository.save(menuCategoryCreateRequest.toEntity(shopId));
    }
}