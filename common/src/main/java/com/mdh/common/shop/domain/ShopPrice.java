package com.mdh.common.shop.domain;

import com.mdh.common.menu.domain.MealType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class ShopPrice {
    @Column(name = "shop_min_price", nullable = false)
    private int shopMinPrice;

    @Column(name = "shop_max_price", nullable = false)
    private int shopMaxPrice;

    @Column(name = "lunch_min_price", nullable = false)
    private int lunchMinPrice;

    @Column(name = "lunch_max_price", nullable = false)
    private int lunchMaxPrice;

    @Column(name = "dinner_min_price", nullable = false)
    private int dinnerMinPrice;

    @Column(name = "dinner_max_price", nullable = false)
    private int dinnerMaxPrice;

    @Builder
    public ShopPrice(
            int shopMinPrice,
            int shopMaxPrice,
            int lunchMaxPrice,
            int lunchMinPrice,
            int dinnerMinPrice,
            int dinnerMaxPrice
    ) {
        this.shopMinPrice = shopMinPrice;
        this.shopMaxPrice = shopMaxPrice;
        this.lunchMaxPrice = lunchMaxPrice;
        this.lunchMinPrice = lunchMinPrice;
        this.dinnerMaxPrice = dinnerMaxPrice;
        this.dinnerMinPrice = dinnerMinPrice;
    }

    public void updatePrice(MealType mealType, int price) {
        switch (mealType) {
            case LUNCH -> updateLunchPrice(price);
            case DINNER -> updateDinnerPrice(price);
        }

        updateShopPrice(price);
    }

    private void updateShopPrice(int price) {
        shopMinPrice = Math.min(shopMinPrice, price);
        shopMaxPrice = Math.max(shopMaxPrice, price);
    }

    private void updateLunchPrice(int lunchPrice) {
        lunchMinPrice = (lunchMinPrice == 0) ? Integer.MAX_VALUE : lunchMinPrice;
        lunchMaxPrice = Math.max(lunchMaxPrice, lunchPrice);
        lunchMinPrice = Math.min(lunchMinPrice, lunchPrice);
    }

    private void updateDinnerPrice(int dinnerPrice) {
        dinnerMinPrice = (dinnerMinPrice == 0) ? Integer.MAX_VALUE : dinnerMinPrice;
        dinnerMaxPrice = Math.max(dinnerMaxPrice, dinnerPrice);
        dinnerMinPrice = Math.min(dinnerMinPrice, dinnerPrice);
    }

}