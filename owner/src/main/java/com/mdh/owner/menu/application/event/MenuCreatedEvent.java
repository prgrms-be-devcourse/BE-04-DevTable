package com.mdh.owner.menu.application.event;

import com.mdh.common.menu.domain.Menu;

public record MenuCreatedEvent(
        Menu menu
) {
}
