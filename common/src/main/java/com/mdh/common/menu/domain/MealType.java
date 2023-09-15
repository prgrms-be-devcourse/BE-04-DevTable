package com.mdh.common.menu.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MealType {
    BREAKFAST("아침"), LUNCH("점심"), DINNER("저녁");

    private final String name;
}