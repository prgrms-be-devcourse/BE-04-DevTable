package com.mdh.common.menu.domain.event;

import com.mdh.common.menu.domain.Menu;

public record MenuUpdatedEvent(
        Menu menu
) {
}