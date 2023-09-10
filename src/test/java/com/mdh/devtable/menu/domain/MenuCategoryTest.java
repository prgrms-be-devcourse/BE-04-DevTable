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
        String updatedName = "Updated Main Course";
        String updatedDescription = "Updated description";
        Integer updatedMinPrice = 15;
        Integer updatedMaxPrice = 55;
        String updatedMealType = "Lunch";

        var menuCategory = DataInitializerFactory.menuCategory(shopId);

        // when
        menuCategory.updateMenuCategory(updatedName,
                updatedDescription,
                updatedMinPrice,
                updatedMaxPrice,
                updatedMealType);

        // Then
        Assertions.assertThat(menuCategory)
                .extracting("name", "description", "minPrice", "maxPrice", "mealType")
                .containsExactly(updatedName, updatedDescription, updatedMinPrice, updatedMaxPrice, updatedMealType);
    }
}