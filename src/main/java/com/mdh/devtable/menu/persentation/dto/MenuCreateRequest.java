package com.mdh.devtable.menu.persentation.dto;

import com.mdh.devtable.menu.domain.MealType;
import com.mdh.devtable.menu.domain.Menu;
import com.mdh.devtable.menu.domain.MenuType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MenuCreateRequest(

        @NotNull(message = "Category ID must not be null")
        Long categoryId,

        @NotBlank(message = "Menu name must not be blank")
        @Size(max = 31, message = "Menu name must be less than or equal to 31 characters")
        String menuName,

        @Min(value = 0, message = "Price must be zero or greater")
        int price,

        @Size(max = 63, message = "Description must be less than or equal to 63 characters")
        String description,

        @Size(max = 15, message = "Label must be less than or equal to 15 characters")
        String label,

        @NotNull(message = "Menu type must not be null")
        MenuType menuType,

        @NotNull(message = "Meal type must not be null")
        MealType mealType
) {
    public Menu toEntity() {
        return Menu.builder()
                .categoryId(categoryId)
                .menuName(menuName)
                .price(price)
                .description(description)
                .label(label)
                .menuType(menuType)
                .mealType(mealType)
                .build();
    }
}