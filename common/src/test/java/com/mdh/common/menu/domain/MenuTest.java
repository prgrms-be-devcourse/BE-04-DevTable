package com.mdh.common.menu.domain;

import com.mdh.common.DataInitializerFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MenuTest {

    @DisplayName("메뉴를 업데이트 할 수 있다.")
    @Test
    void update() {
        //given
        var menu = DataInitializerFactory.menu();
        var updatedMenuName = "Updated Menu";
        var updatedPrice = 200;
        var updatedDescription = "Updated Description";
        var updatedLabel = "Updated Label";
        var updatedMenuType = MenuType.APPETIZER;
        var updatedMealType = MealType.LUNCH;

        // wheen
        menu.update(updatedMenuName, updatedPrice, updatedDescription, updatedLabel, updatedMenuType, updatedMealType);

        // then
        Assertions.assertThat(menu)
                .extracting(Menu::getMenuName, Menu::getPrice, Menu::getDescription, Menu::getLabel, Menu::getMenuType, Menu::getMealType)
                .containsExactly(updatedMenuName, updatedPrice, updatedDescription, updatedLabel, updatedMenuType, updatedMealType);

    }
}