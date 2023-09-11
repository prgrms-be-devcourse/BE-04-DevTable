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

    @DisplayName("메뉴 카테고리의 최소 가격을 업데이트한다.")
    @Test
    public void updateMinPriceTest() {
        // given
        var shopId = 1L;
        var updatedMinPrice = 20;

        var menuCategory = DataInitializerFactory.menuCategory(shopId);

        // when
        menuCategory.updateMinPrice(updatedMinPrice);

        // then
        Assertions.assertThat(menuCategory)
                .extracting("minPrice")
                .isEqualTo(updatedMinPrice);
    }

    @DisplayName("메뉴 카테고리의 최대 가격을 업데이트한다.")
    @Test
    public void updateMaxPriceTest() {
        // given
        var shopId = 1L;
        var updatedMaxPrice = 50;

        var menuCategory = DataInitializerFactory.menuCategory(shopId);

        // when
        menuCategory.updateMaxPrice(updatedMaxPrice);

        // then
        Assertions.assertThat(menuCategory)
                .extracting("maxPrice")
                .isEqualTo(updatedMaxPrice);
    }
}