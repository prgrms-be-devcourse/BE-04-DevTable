package com.mdh.common.menu.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MenuType {
    APPETIZER("에피타이저"), DRINK("음료"), MAIN("메인"), DESSERT("디저트");

    private final String name;
}