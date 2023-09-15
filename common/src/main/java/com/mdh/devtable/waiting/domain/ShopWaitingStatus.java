package com.mdh.devtable.waiting.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ShopWaitingStatus {
    OPEN("영업 중"),
    BREAK_TIME("브레이크 타임"),
    CLOSE("영업 종료");

    private final String statusMessage;

    public boolean isCloseWaitingStatus() {
        return this == ShopWaitingStatus.CLOSE;
    }
}
