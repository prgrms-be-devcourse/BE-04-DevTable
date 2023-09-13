package com.mdh.devtable.menu.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "menus")
@Entity
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "menu_name", nullable = false, length = 31)
    private String menuName;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "description", nullable = true, length = 63)
    private String description;

    @Column(name = "label", nullable = true, length = 15)
    private String label;

    @Enumerated(EnumType.STRING)
    @Column(name = "menu_type", nullable = false, length = 15)
    private MenuType menuType;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type", nullable = false, length = 7)
    private MealType mealType;

    @Builder
    public Menu(Long categoryId,
                String menuName,
                int price,
                String description,
                String label,
                MenuType menuType,
                MealType mealType) {
        this.categoryId = categoryId;
        this.menuName = menuName;
        this.price = price;
        this.description = description;
        this.label = label;
        this.menuType = menuType;
        this.mealType = mealType;
    }

    public void update(String menuName,
                       int price,
                       String description,
                       String label,
                       MenuType menuType,
                       MealType mealType) {
        this.menuName = menuName;
        this.price = price;
        this.description = description;
        this.label = label;
        this.menuType = menuType;
        this.mealType = mealType;
    }
}