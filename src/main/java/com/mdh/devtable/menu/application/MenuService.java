package com.mdh.devtable.menu.application;

import com.mdh.devtable.menu.application.event.MenuCreatedEvent;
import com.mdh.devtable.menu.infra.persistence.MenuCategoryRepository;
import com.mdh.devtable.menu.persentation.dto.MenuCategoryCreateRequest;
import com.mdh.devtable.menu.persentation.dto.MenuCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class MenuService {

    private final MenuCategoryRepository menuCategoryRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Long createMenu(Long categoryId, MenuCreateRequest menuCreateRequest) {
        var menuCategory = menuCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new NoSuchElementException("등록된 카테고리 ID가 없습니다." + categoryId));
        var menu = menuCreateRequest.toEntity();
        menuCategory.addMenu(menu);
        eventPublisher.publishEvent(new MenuCreatedEvent(menu));

        return menu.getId();
    }

    @Transactional
    public Long createMenuCategory(Long shopId, MenuCategoryCreateRequest menuCategoryCreateRequest) {
        var menuCategory = menuCategoryCreateRequest.toEntity(shopId);
        return menuCategoryRepository.save(menuCategory).getId();
    }
}