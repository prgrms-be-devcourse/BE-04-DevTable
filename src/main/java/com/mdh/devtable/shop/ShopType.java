package com.mdh.devtable.shop;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ShopType {
    KOREAN("한식"),
    CHINESE("중식"),
    JAPANESE("일식"),
    ASIAN("아시안음식"),
    FRANCE("프랑스음식"),
    ITALIAN("이탈리아음식"),
    SPAIN("스페인음식"),
    EUROPEAN("유러피안음식"),
    FUSION("퓨젼음식"),
    AMERICAN("아메리칸음식"),
    EXTRA("기타 세계음식"),
    CONTEMPORARY("컨템포러리");

    private final String name;
}