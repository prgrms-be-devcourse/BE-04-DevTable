package com.mdh.owner.menu.presentation.dto;

import com.mdh.common.menu.domain.MealType;
import com.mdh.common.menu.domain.Menu;
import com.mdh.common.menu.domain.MenuType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MenuCreateRequest(

        @NotBlank(message = "메뉴 이름을 입력해 주세요.")
        @Size(max = 31, message = "메뉴 이름은 31글자 이하로 작성해 주세요.")
        String menuName,

        @Min(value = 0, message = "가격은 0 이상으로 작성해 주세요.")
        int price,

        @Size(max = 63, message = "메뉴 설명은 63글자 이하로 작성해 주세요.")
        String description,

        @Size(max = 15, message = "라벨은 15글자 이하로 작성해 주세요.")
        String label,

        @NotNull(message = "메뉴 타입을 입력해 주세요.")
        MenuType menuType,

        @NotNull(message = "식사 타입을 입력해 주세요.")
        MealType mealType
) {
    public Menu toEntity() {
        return Menu.builder()
                .menuName(menuName)
                .price(price)
                .description(description)
                .label(label)
                .menuType(menuType)
                .mealType(mealType)
                .build();
    }
}
