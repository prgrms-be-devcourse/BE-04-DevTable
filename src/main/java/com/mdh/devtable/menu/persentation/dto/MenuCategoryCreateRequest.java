package com.mdh.devtable.menu.persentation.dto;

import com.mdh.devtable.menu.domain.MealType;
import com.mdh.devtable.menu.domain.MenuCategory;
import com.mdh.devtable.menu.domain.MenuType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record MenuCategoryCreateRequest(

        @NotNull(message = "메뉴 카테고리 이름을 입력해 주세요")
        String name,

        String description,

        @Min(value = 1, message = "1 이상의 값을 입력해 주세요")
        Integer minPrice,

        @Min(value = 1, message = "1 이상의 값을 입력해 주세요")
        Integer maxPrice,

        @NotNull(message = "메뉴 타입을 입력해 주세요(APPETIZER, DRINK, MAIN)")
        MenuType menuType,

        @NotNull(message = "식사 타입을 입력해 주세요")
        MealType mealType

) {

    public MenuCategory toEntity(Long shopId) {
        return MenuCategory.builder()
                .shopId(shopId)
                .name(name)
                .description(description)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .mealType(mealType)
                .menuType(menuType)
                .build();
    }
}