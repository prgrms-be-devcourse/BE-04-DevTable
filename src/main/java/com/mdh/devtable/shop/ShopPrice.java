package com.mdh.devtable.shop;

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
        int lunchMaxPrice,
        int lunchMinPrice,
        int dinnerMinPrice,
        int dinnerMaxPrice
    ) {
        this.lunchMaxPrice = lunchMaxPrice;
        this.lunchMinPrice = lunchMinPrice;
        this.dinnerMaxPrice = dinnerMaxPrice;
        this.dinnerMinPrice = dinnerMinPrice;
    }
    // TODO 메뉴 변경 시 가게의 가격 변경
    /*
    public ShopPrice updateShopPrice(Menu menu) {
        switch (menu.MenuType) {
            LUNCH -> {
                updateLunchMenuPrice(menu.price);
            }
            DINNER -> {
                updateDinnerMenuPrice(menu.price);
            }
        }

        return ShopPrice.builder()
            .lunchMinPrice(this.lunchMinPrice)
            .lunchMaxPrice(this.lunchMaxPrice)
            .dinnerMinPrice(this.dinnerMinPrice)
            .dinnerMaxPrice(this.dinnerMaxPrice)
            .build();
    }

    private void updateLunchMenuPrice(int price) {

    }

    private void updateDinnerMenuPrice(int price) {

    }
    */
}