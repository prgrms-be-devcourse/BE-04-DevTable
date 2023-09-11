package com.mdh.devtable.menu.domain;

import com.mdh.devtable.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "categories")
@Entity
public class MenuCategory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shop_id", nullable = false)
    private Long shopId;

    @Column(name = "name", nullable = false, length = 31)
    private String name;

    @Column(name = "description", length = 63)
    private String description;

    @Column(name = "min_price", nullable = false)
    private int minPrice;

    @Column(name = "max_price", nullable = false)
    private int maxPrice;

    @Column(name = "menu_type", nullable = false, length = 15)
    private String menuType;

    @Column(name = "meal_type", nullable = false, length = 7)
    private String mealType;

    @Builder
    public MenuCategory(@NonNull Long shopId,
                        @NonNull String name,
                        @NonNull String description,
                        @NonNull Integer minPrice,
                        @NonNull Integer maxPrice,
                        @NonNull String menuType,
                        @NonNull String mealType) {
        this.shopId = shopId;
        this.name = name;
        this.description = description;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.menuType = menuType;
        this.mealType = mealType;
    }

    public void updateMenuCategory(String name,
                                   String description,
                                   Integer minPrice,
                                   Integer maxPrice,
                                   String mealType) {
        this.name = name;
        this.description = description;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.mealType = mealType;
    }

}