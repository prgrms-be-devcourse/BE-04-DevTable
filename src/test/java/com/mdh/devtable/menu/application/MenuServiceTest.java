package com.mdh.devtable.menu.application;

import com.mdh.devtable.DataInitializerFactory;
import com.mdh.devtable.menu.domain.MealType;
import com.mdh.devtable.menu.domain.Menu;
import com.mdh.devtable.menu.domain.MenuType;
import com.mdh.devtable.menu.infra.persistence.MenuRepository;
import com.mdh.devtable.menu.persentation.dto.MenuCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @InjectMocks
    private MenuService menuService;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private MenuServiceValidator menuServiceValidator;

    @DisplayName("점주는 메뉴를 저장할 수 있다.")
    @Test
    void createMenu() {
        // given
        var categoryId = 1L;
        var menu = DataInitializerFactory.menu(categoryId);
        var request = new MenuCreateRequest(
                categoryId,
                "Spaghetti",
                15000,
                "Delicious spaghetti with tomato sauce",
                "Popular",
                MenuType.MAIN,
                MealType.DINNER
        );
        given(menuRepository.save(any(Menu.class))).willReturn(menu);

        // when
        menuService.createMenu(request);

        // then
        verify(menuRepository, times(1)).save(any(Menu.class));
    }

    @DisplayName("등록되지 않은 카테고리 ID로 메뉴를 저장하려고 하면 예외가 발생한다.")
    @Test
    void createMenuWithInvalidCategoryId() {
        // Given
        var invalidCategoryId = -1L;
        var request = new MenuCreateRequest(
                invalidCategoryId,
                "Spaghetti",
                15000,
                "Delicious spaghetti with tomato sauce",
                "Popular",
                MenuType.MAIN,
                MealType.DINNER
        );
        doThrow(new NoSuchElementException("등록된 카테고리 ID가 없습니다."))
                .when(menuServiceValidator).validateMenuCreate(invalidCategoryId);

        // When & Then
        assertThrows(NoSuchElementException.class, () -> menuService.createMenu(request));
    }
}