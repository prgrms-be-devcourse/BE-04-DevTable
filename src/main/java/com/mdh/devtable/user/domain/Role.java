package com.mdh.devtable.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    GUEST("ROLE_GUEST", "손님"),
    OWNER("ROLE_OWNER", "점주");

    private final String key;
    private final String title;
}