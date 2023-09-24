package com.mdh.owner.menu.application.dto;

import com.mdh.common.menu.domain.MealType;
import com.mdh.common.menu.domain.Menu;
import com.mdh.common.menu.domain.MenuCategory;
import com.mdh.common.menu.domain.MenuType;

import java.util.List;
import java.util.stream.Collectors;

public record MenuCategoriesResponse(
        Long menuCategoryId,
        String name,
        String description,
        int minPrice,
        int maxPrice,
        List<MenuResponse> menus
) {
    public static MenuCategoriesResponse from(MenuCategory menuCategory) {
        List<MenuResponse> menuResponses = menuCategory.getMenus()
                .stream()
                .map(MenuResponse::from)
                .toList();

        return new MenuCategoriesResponse(
                menuCategory.getId(),
                menuCategory.getName(),
                menuCategory.getDescription(),
                menuCategory.getMinPrice(),
                menuCategory.getMaxPrice(),
                menuResponses
        );
    }

    public record MenuResponse(
            Long id,
            String menuName,
            int price,
            String description,
            String label,
            MenuType menuType,
            MealType mealType
    ) {
        public static MenuResponse from(Menu menu) {
            return new MenuResponse(
                    menu.getId(),
                    menu.getMenuName(),
                    menu.getPrice(),
                    menu.getDescription(),
                    menu.getLabel(),
                    menu.getMenuType(),
                    menu.getMealType()
            );
        }
    }
}