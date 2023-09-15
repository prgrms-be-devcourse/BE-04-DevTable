package com.mdh.devtable.menu.domain;

import com.mdh.devtable.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "menus")
@Entity
public class Menu extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private MenuCategory menuCategory;

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
    public Menu(String menuName,
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