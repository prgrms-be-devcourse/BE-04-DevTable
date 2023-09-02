package com.mdh.devtable.shop;

import com.mdh.devtable.global.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@Table(name = "shops")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Shop extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @ManyToOne
    @JoinColumn(name = "region_id", referencedColumnName = "id")
    private Region region;

    @Builder
    public Shop(@NonNull String name,
                @NonNull String description,
                @NonNull ShopType shopType,
                @NonNull ShopDetails shopDetails,
                @NonNull ShopAddress shopAddress,
                @NonNull Region region) {
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

    // TODO 메뉴 변경 시 가게의 가격 변경

    /*
    public void updateShopPrice(Menu menu) {
        this.shopPrice = this.shopPrice.updateShopPrice(menu);
    }
    */

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