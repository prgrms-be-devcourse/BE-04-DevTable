package com.mdh.devtable.menu.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MenuType {
    APPETIZER("에피타이저"), DRINK("음료"), MAIN("메인"), DESSERT("디저트");

    private final String name;
}