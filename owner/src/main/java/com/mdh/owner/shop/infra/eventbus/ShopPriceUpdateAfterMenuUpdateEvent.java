package com.mdh.owner.shop.infra.eventbus;

import com.mdh.owner.menu.application.event.MenuUpdatedEvent;
import com.mdh.common.shop.persistence.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Component
public class ShopPriceUpdateAfterMenuUpdateEvent {

    private final ShopRepository shopRepository;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void updateShopPrice(MenuUpdatedEvent event) {
        var menu = event.menu();
        var shopId = menu.getMenuCategory().getShopId();

        var shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 매장이 존재하지 않습니다." + shopId));

        shop.getShopPrice()
                .updatePrice(menu.getMealType(), menu.getPrice());
    }
}
