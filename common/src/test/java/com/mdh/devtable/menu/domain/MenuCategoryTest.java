package com.mdh.devtable.menu.domain;

import com.mdh.devtable.DataInitializerFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MenuCategoryTest {

    @DisplayName("메뉴 카테고리 정보를 업데이트한다.")
    @Test
    public void updateMenuCategoryTest() {
        // given
        var shopId = 1L;
        var updatedName = "Updated Main Course";
        var updatedDescription = "Updated description";

        var menuCategory = DataInitializerFactory.menuCategory(shopId);

        // when
        menuCategory.updateMenuCategory(updatedName, updatedDescription);

        // Then
        Assertions.assertThat(menuCategory)
                .extracting("name", "description")
                .containsExactly(updatedName, updatedDescription);
    }

    @DisplayName("메뉴 카테고리에 메뉴를 추가하면 메뉴 카테고리의 가격이 갱신된다.")
    @Test
    public void addMenuTest() {
        // Given
        Long shopId = 1L;
        MenuCategory menuCategory = new MenuCategory(shopId, "Main Course", "Delicious main courses");
        Menu menu = new Menu(); // 가정: Menu 객체가 적절하게 초기화되어 있음

        // When
        menuCategory.addMenu(menu);

        // Then
        Assertions.assertThat(menuCategory.getMenus()).contains(menu);
        Assertions.assertThat(menu.getMenuCategory()).isEqualTo(menuCategory);
    }

}