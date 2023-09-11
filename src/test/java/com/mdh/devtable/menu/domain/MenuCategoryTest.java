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
        var updatedMinPrice = 15;
        var updatedMaxPrice = 55;
        var updatedMealType = MealType.LUNCH;
        var updatedMenuType = MenuType.APPETIZER;

        var menuCategory = DataInitializerFactory.menuCategory(shopId);

        // when
        menuCategory.updateMenuCategory(updatedName,
                updatedDescription,
                updatedMinPrice,
                updatedMaxPrice,
                updatedMealType,
                updatedMenuType);

        // Then
        Assertions.assertThat(menuCategory)
                .extracting("name", "description", "minPrice", "maxPrice", "mealType", "menuType")
                .containsExactly(updatedName, updatedDescription, updatedMinPrice, updatedMaxPrice, updatedMealType, updatedMenuType);
    }
}