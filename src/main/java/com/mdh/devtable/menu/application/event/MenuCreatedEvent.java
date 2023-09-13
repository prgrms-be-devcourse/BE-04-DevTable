package com.mdh.devtable.menu.application.event;

import com.mdh.devtable.menu.domain.Menu;

public record MenuCreatedEvent(
        Menu menu
) {
}