package com.mdh.owner.shop.infra.eventbus;

import com.mdh.owner.DataInitializerFactory;
import com.mdh.common.menu.domain.event.MenuCreatedEvent;
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
class ShopPriceUpdateAfterMenuCreatedEventTest {

    @InjectMocks
    private ShopPriceUpdateAfterMenuCreatedEvent shopPriceUpdateAfterMenuCreatedEvent;

    @Mock
    private ShopRepository shopRepository;

    @Mock
    private Shop shop;

    @Mock
    private ShopPrice shopPrice;

    @DisplayName("메뉴가 등록된 이후 매장의 최소 가격, 최대 가격은 업데이트 된다.")
    @Test
    void updateShopPrice() {
        // Given
        Long shopId = 1L;
        var menu = DataInitializerFactory.menu();
        var category = DataInitializerFactory.menuCategory(shopId);
        category.addMenu(menu);
        MenuCreatedEvent event = new MenuCreatedEvent(menu);

        given(shopRepository.findById(shopId)).willReturn(Optional.of(shop));
        given(shop.getShopPrice()).willReturn(shopPrice);

        // When
        shopPriceUpdateAfterMenuCreatedEvent.updateShopPrice(event);

        // Then
        then(shopPrice).should().updatePrice(menu.getMealType(), menu.getPrice());
    }
}