package com.mdh.devtable.menu.domain;

import com.mdh.devtable.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

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

    @Column(name = "description", nullable = true, length = 63)
    private String description;

    @Column(name = "min_price", nullable = false)
    private int minPrice;

    @Column(name = "max_price", nullable = false)
    private int maxPrice;

    @Version
    private Long version;

    public MenuCategory(@NonNull Long shopId, @NonNull String name, String description) {
        this.shopId = shopId;
        this.name = name;
        this.description = description;
    }

    public void updateMenuCategory(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void updateMinPrice(int minPrice) {
        this.minPrice = minPrice;
    }

    public void updateMaxPrice(int maxPrice) {
        this.maxPrice = maxPrice;
    }

}