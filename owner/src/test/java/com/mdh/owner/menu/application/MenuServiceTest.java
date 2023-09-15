package com.mdh.owner.menu.application;

import com.mdh.owner.DataInitializerFactory;
import com.mdh.owner.menu.application.event.MenuCreatedEvent;
import com.mdh.common.menu.domain.MealType;
import com.mdh.common.menu.domain.MenuCategory;
import com.mdh.common.menu.domain.MenuType;
import com.mdh.common.menu.persistence.MenuCategoryRepository;
import com.mdh.common.menu.persistence.MenuRepository;
import com.mdh.owner.menu.presentation.dto.MenuCategoryCreateRequest;
import com.mdh.owner.menu.presentation.dto.MenuCategoryUpdateRequest;
import com.mdh.owner.menu.presentation.dto.MenuCreateRequest;
import com.mdh.owner.menu.presentation.dto.MenuUpdateRequest;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    @Mock
    private MenuRepository menuRepository;

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
        assertThatThrownBy(() -> menuService.createMenu(invalidCategoryId, request))
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
        menuService.updateMenuCategory(categoryId, request);

        // then
        verify(menuCategoryRepository, times(1)).findById(categoryId);
        Assertions.assertThat(menuCategory)
                .extracting("name", "description")
                .containsExactly(request.name(), request.description());
    }


    @DisplayName("점주는 메뉴 카테고리를 삭제할 수 있다.")
    @Test
    void deleteMenuCategory() {
        // Given
        Long shopId = 1L;
        Long categoryId = 1L;
        var menuCategory = DataInitializerFactory.menuCategory(shopId);
        given(menuCategoryRepository.findById(categoryId)).willReturn(Optional.of(menuCategory));

        // When
        menuService.deleteMenuCategory(categoryId);

        // Then
        verify(menuCategoryRepository, times(1)).delete(menuCategory);
    }

    @DisplayName("존재하지 않는 카테고리 ID로 메뉴 카테고리를 삭제하려고 하면 예외가 발생한다.")
    @Test
    void deleteMenuCategoryWithInvalidCategoryId() {
        // Given
        Long invalidCategoryId = -1L;
        given(menuCategoryRepository.findById(invalidCategoryId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> menuService.deleteMenuCategory(invalidCategoryId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("등록된 카테고리 ID가 없습니다.");
    }

    @DisplayName("점주는 메뉴를 수정 할 수 있다.")
    @Test
    void updateMenu() {
        var menuUpdateRequest = new MenuUpdateRequest(
                "New Name",
                200,
                "New Description",
                "New Label",
                MenuType.MAIN,
                MealType.DINNER
        );
        Long menuId = 1L;
        var menu = DataInitializerFactory.menu();

        given(menuRepository.findById(any(Long.class))).willReturn(Optional.of(menu));

        //when
        menuService.updateMenu(menuId, menuUpdateRequest);

        //then
        verify(menuRepository, times(1)).findById(menuId);
        Assertions.assertThat(menu)
                .extracting("menuName", "price", "description", "label", "menuType", "mealType")
                .containsExactly(
                        menuUpdateRequest.menuName(),
                        menuUpdateRequest.price(),
                        menuUpdateRequest.description(),
                        menuUpdateRequest.label(),
                        menuUpdateRequest.menuType(),
                        menuUpdateRequest.mealType()
                );

    }
}
