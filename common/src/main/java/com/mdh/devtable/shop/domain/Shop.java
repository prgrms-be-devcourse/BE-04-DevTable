package com.mdh.devtable.shop.domain;

import com.mdh.devtable.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Table(name = "shops")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Shop extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "region_id", referencedColumnName = "id")
    private Region region;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "name", length = 63, nullable = false)
    private String name;

    @Column(name = "description", length = 127, nullable = false)
    private String description;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "shop_type", length = 31, nullable = false)
    private ShopType shopType;

    @Embedded
    private ShopDetails shopDetails;

    @Embedded
    private ShopPrice shopPrice;

    @Column(name = "bookmark_count", nullable = false)
    private int bookmarkCount;

    @Embedded
    private ShopAddress shopAddress;

    @Builder
    public Shop(
            @NonNull Long userId,
            @NonNull String name,
            @NonNull String description,
            @NonNull ShopType shopType,
            @NonNull ShopDetails shopDetails,
            @NonNull ShopAddress shopAddress,
            @NonNull Region region) {
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.shopType = shopType;
        this.shopDetails = shopDetails;
        this.shopPrice = ShopPrice.builder().build();
        this.bookmarkCount = 0;
        this.shopAddress = shopAddress;
        this.region = region;
    }

    // 비즈니스 메서드
    public void update(
            @NonNull String name,
            @NonNull String description,
            @NonNull ShopType shopType,
            @NonNull ShopDetails shopDetails,
            @NonNull ShopAddress shopAddress,
            @NonNull Region region
    ) {
        this.name = name;
        this.description = description;
        this.shopType = shopType;
        this.shopDetails = shopDetails;
        this.shopAddress = shopAddress;
        this.region = region;
    }

    public void increaseBookmarkCount() {
        this.bookmarkCount++;
    }

    public void decreaseBookmarkCount() {
        if (this.bookmarkCount == 0) {
            throw new IllegalStateException("북마크 개수가 0 일 때 북마크 개수를 줄이는 것은 불가능합니다.");
        }
        this.bookmarkCount--;
    }
}