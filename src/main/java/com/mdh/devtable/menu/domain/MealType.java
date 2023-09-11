package com.mdh.devtable.menu.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MealType {
    BREAK_FAST("아침"), LUNCH("점심"), DINNER("저녁");

    private final String name;
}