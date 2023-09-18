package com.mdh.owner.menu.application;

import com.mdh.common.menu.domain.event.MenuCreatedEvent;
import com.mdh.common.menu.domain.event.MenuUpdatedEvent;
import com.mdh.common.menu.persistence.MenuCategoryRepository;
import com.mdh.common.menu.persistence.MenuRepository;
import com.mdh.owner.menu.presentation.dto.MenuCategoryCreateRequest;
import com.mdh.owner.menu.presentation.dto.MenuCategoryUpdateRequest;
import com.mdh.owner.menu.presentation.dto.MenuCreateRequest;
import com.mdh.owner.menu.presentation.dto.MenuUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class MenuService {

    private final MenuCategoryRepository menuCategoryRepository;
    private final MenuRepository menuRepository;
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
    public void updateMenu(Long menuId, MenuUpdateRequest menuUpdateRequest) {
        var menu = menuRepository.findById(menuId).orElseThrow(() -> new NoSuchElementException("등록된 메뉴 ID가 없습니다." + menuId));
        menu.update(menuUpdateRequest.menuName(),
                menuUpdateRequest.price(),
                menuUpdateRequest.description(),
                menuUpdateRequest.label(),
                menuUpdateRequest.menuType(),
                menuUpdateRequest.mealType());
        eventPublisher.publishEvent(new MenuUpdatedEvent(menu));
    }

    @Transactional
    public Long createMenuCategory(Long shopId, MenuCategoryCreateRequest menuCategoryCreateRequest) {
        var menuCategory = menuCategoryCreateRequest.toEntity(shopId);
        return menuCategoryRepository.save(menuCategory).getId();
    }

    @Transactional
    public void updateMenuCategory(Long categoryId, MenuCategoryUpdateRequest request) {
        var menuCategory = menuCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new NoSuchElementException("등록된 카테고리 ID가 없습니다." + categoryId));

        menuCategory.updateMenuCategory(request.name(), request.description());
    }

    @Transactional
    public void deleteMenuCategory(Long categoryId) {
        var menuCategory = menuCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new NoSuchElementException("등록된 카테고리 ID가 없습니다." + categoryId));
        menuCategoryRepository.delete(menuCategory);
    }
}