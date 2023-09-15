package com.mdh.owner.shop.infra.eventbus;

import com.mdh.owner.DataInitializerFactory;
import com.mdh.owner.menu.application.event.MenuUpdatedEvent;
import com.mdh.common.shop.domain.Shop;
import com.mdh.common.shop.domain.ShopPrice;
import com.mdh.common.shop.persistence.ShopRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ShopPriceUpdateAfterMenuUpdateEventTest {

    @InjectMocks
    private ShopPriceUpdateAfterMenuUpdateEvent shopPriceUpdateAfterMenuUpdateEvent;

    @Mock
    private ShopRepository shopRepository;

    @Mock
    private Shop shop;

    @Mock
    private ShopPrice shopPrice;

    @DisplayName("메뉴가 업데이트된 이후 매장의 가격은 업데이트 된다.")
    @Test
    void updateShopPrice() {
        // Given
        var shopId = 1L;
        var menu = DataInitializerFactory.menu();
        var category = DataInitializerFactory.menuCategory(shopId);
        category.addMenu(menu);
        var event = new MenuUpdatedEvent(menu);

        given(shopRepository.findById(shopId)).willReturn(Optional.of(shop));
        given(shop.getShopPrice()).willReturn(shopPrice);

        // When
        shopPriceUpdateAfterMenuUpdateEvent.updateShopPrice(event);

        // Then
        then(shopPrice).should().updatePrice(menu.getMealType(), menu.getPrice());
    }
}