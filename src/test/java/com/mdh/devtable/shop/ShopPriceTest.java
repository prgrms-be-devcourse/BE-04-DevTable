package com.mdh.devtable.shop;

import com.mdh.devtable.menu.domain.MealType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ShopPriceTest {

    @DisplayName("점심 가격 업데이트")
    @Test
    void updateLunchPrice() {
        // Given
        ShopPrice shopPrice = ShopPrice.builder()
                .lunchMaxPrice(10000)
                .lunchMinPrice(5000)
                .build();

        int newPrice = 12000;

        // When
        shopPrice.updatePrice(MealType.LUNCH, newPrice);

        // Then
        assertThat(shopPrice.getLunchMaxPrice()).isEqualTo(newPrice);
        assertThat(shopPrice.getLunchMinPrice()).isEqualTo(5000);
    }

    @DisplayName("저녁 가격 업데이트")
    @Test
    void updateDinnerPrice() {
        // Given
        ShopPrice shopPrice = ShopPrice.builder()
                .dinnerMaxPrice(20000)
                .dinnerMinPrice(10000)
                .build();

        int newPrice = 8000;

        // When
        shopPrice.updatePrice(MealType.DINNER, newPrice);

        // Then
        assertThat(shopPrice.getDinnerMaxPrice()).isEqualTo(20000);
        assertThat(shopPrice.getDinnerMinPrice()).isEqualTo(newPrice);
    }
}