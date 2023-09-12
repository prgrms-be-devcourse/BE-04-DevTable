package com.mdh.devtable.menu.application;

import com.mdh.devtable.DataInitializerFactory;
import com.mdh.devtable.menu.application.event.MenuCreatedEvent;
import com.mdh.devtable.menu.domain.MealType;
import com.mdh.devtable.menu.domain.MenuCategory;
import com.mdh.devtable.menu.domain.MenuType;
import com.mdh.devtable.menu.infra.persistence.MenuCategoryRepository;
import com.mdh.devtable.menu.persentation.dto.MenuCategoryCreateRequest;
import com.mdh.devtable.menu.persentation.dto.MenuCategoryUpdateRequest;
import com.mdh.devtable.menu.persentation.dto.MenuCreateRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @InjectMocks
    private MenuService menuService;

    @Mock
    private MenuCategoryRepository menuCategoryRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @DisplayName("점주는 메뉴를 저장할 수 있다.")
    @Test
    void createMenu() {
        // given
        var categoryId = 1L;
        var menuCategory = DataInitializerFactory.menuCategory(categoryId);
        var request = new MenuCreateRequest(
                "Spaghetti",
                15000,
                "Delicious spaghetti with tomato sauce",
                "Popular",
                MenuType.MAIN,
                MealType.DINNER
        );
        given(menuCategoryRepository.findById(categoryId)).willReturn(Optional.of(menuCategory));

        // when
        menuService.createMenu(categoryId, request);

        // then
        verify(eventPublisher, times(1)).publishEvent(any(MenuCreatedEvent.class));
    }

    @DisplayName("등록되지 않은 카테고리 ID로 메뉴를 저장하려고 하면 예외가 발생한다.")
    @Test
    void createMenuWithInvalidCategoryId() {
        // Given
        var invalidCategoryId = -1L;
        var request = new MenuCreateRequest(
                "Spaghetti",
                15000,
                "Delicious spaghetti with tomato sauce",
                "Popular",
                MenuType.MAIN,
                MealType.DINNER
        );
        given(menuCategoryRepository.findById(invalidCategoryId)).willReturn(Optional.empty());

        // When & Then
        Assertions.assertThatThrownBy(() -> menuService.createMenu(invalidCategoryId, request))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("등록된 카테고리 ID가 없습니다.");
    }

    @DisplayName("점주는 메뉴 카테고리를 저장할 수 있다.")
    @Test
    void createMenuCategory() {
        //given
        var shopId = 1L;
        var menuCategory = DataInitializerFactory.menuCategory(shopId);
        var request = new MenuCategoryCreateRequest("Main Course", "Delicious main courses");
        given(menuCategoryRepository.save(any(MenuCategory.class))).willReturn(menuCategory);

        //when
        menuService.createMenuCategory(shopId, request);

        //then
        verify(menuCategoryRepository, times(1)).save(any(MenuCategory.class));
    }

    @DisplayName("점주는 메뉴 카테고리를 업데이트할 수 있다.")
    @Test
    void updateMenuCategory() {
        // given
        var shopId = 1L;
        var categoryId = 1L;
        var menuCategory = DataInitializerFactory.menuCategory(shopId);
        var request = new MenuCategoryUpdateRequest("Updated Main Course", "Updated description");
        given(menuCategoryRepository.findById(categoryId)).willReturn(Optional.of(menuCategory));

        // when
        menuService.updateMenuCategory(shopId, categoryId, request);

        // then
        verify(menuCategoryRepository, times(1)).findById(categoryId);
        Assertions.assertThat(menuCategory)
                .extracting("name", "description")
                .containsExactly(request.name(), request.description());
    }
}