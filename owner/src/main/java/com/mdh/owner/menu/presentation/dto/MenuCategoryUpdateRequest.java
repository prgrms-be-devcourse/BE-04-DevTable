package com.mdh.owner.menu.presentation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MenuCategoryUpdateRequest(

        @Size(min = 1, max = 31, message = "메뉴 카테고리 이름은 1~31자 이내로 입력해 주세요")
        @NotNull(message = "메뉴 카테고리 이름을 입력해 주세요")
        String name,

        @Size(max = 31, message = "메뉴 카테고리 설명은 31자 이내로 해주세요")
        String description
) {
}
