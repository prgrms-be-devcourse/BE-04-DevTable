package com.mdh.common.shop.domain;

import com.mdh.common.DataInitializerFactory;
import org.hibernate.AssertionFailure;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ShopTest {

    @Test
    @DisplayName("Shop을 생성하면 북마크 수는 0이어야 한다")
    void shopConstructorTest() {
        // given
        var userId = 1L;
        var shopDetails = DataInitializerFactory.shopDetails();
        var shopAddress = DataInitializerFactory.shopAddress();
        var region = DataInitializerFactory.region();

        // when
        var shop = DataInitializerFactory.shop(userId, shopDetails, region, shopAddress);

        // then
        assertThat(shop)
                .extracting(Shop::getBookmarkCount,
                        Shop::getName,
                        Shop::getDescription,
                        Shop::getShopType,
                        Shop::getShopDetails,
                        Shop::getShopAddress,
                        Shop::getRegion)
                .containsExactly(0,
                        "가게 이름",
                        "가게의 간단한 설명",
                        ShopType.AMERICAN,
                        shopDetails,
                        shopAddress,
                        region);
    }

    @Disabled
    @Test
    @DisplayName("Shop을 생성할 때 필수값을 넣어주지 않으면 예외가 발생한다.")
    void necessaryFieldExceptionWhenCreateShop() {
        // TODO 도메인 필드 유효성 테스트 이후 작성
        throw new AssertionFailure("필수 필드가 null이면 예외를 던집니다.");
    }

    @Test
    @DisplayName("Shop의 정보를 수정한다.")
    void updateShopInfo() {
        // given
        var userId = 1L;

        var shopDetails = DataInitializerFactory.shopDetails();
        var shopAddress = DataInitializerFactory.shopAddress();
        var region = DataInitializerFactory.region();
        var shop = DataInitializerFactory.shop(userId, shopDetails, region, shopAddress);

        String changeName = "changedName";
        String changeDescription = "changedDescription";
        ShopType changeShopType = ShopType.ASIAN;

        var changeShopDetails = ShopDetails.builder()
                .url("www.change.com")
                .holiday("일요일")
                .openingHours("12시")
                .phoneNumber("01012345678")
                .info("정보")
                .introduce("introduce")
                .build();

        var changeShopAddress = ShopAddress.builder()
                .address("서울시 송파구")
                .zipcode("12345")
                .latitude("123.345")
                .longitude("123.127")
                .build();

        var changeRegion = Region.builder()
                .city("서울")
                .district("송파구")
                .build();

        // when
        shop.update(changeName,
                changeDescription,
                changeShopType,
                changeShopDetails,
                changeShopAddress,
                changeRegion);

        // then
        assertThat(shop)
                .extracting(Shop::getBookmarkCount,
                        Shop::getName,
                        Shop::getDescription,
                        Shop::getShopType,
                        Shop::getShopDetails,
                        Shop::getShopAddress,
                        Shop::getRegion)
                .containsExactly(0,
                        changeName,
                        changeDescription,
                        changeShopType,
                        changeShopDetails,
                        changeShopAddress,
                        changeRegion);
    }

    @Disabled
    @Test
    @DisplayName("Shop의 정보를 수정할 때 필수값을 넣어주지 않으면 예외가 발생한다.")
    void necessaryFieldExceptionWhenUpdateShop() {
        // TODO 도메인 필드 유효성 테스트 이후 작성
        throw new AssertionFailure("필수 필드가 null이면 예외를 던집니다.");
    }


    @Test
    @DisplayName("북마크 개수를 증가시킨다.")
    void increaseBookMarks() {
        // given
        var userId = 1L;
        var shopDetails = DataInitializerFactory.shopDetails();
        var shopAddress = DataInitializerFactory.shopAddress();
        var region = DataInitializerFactory.region();
        var shop = DataInitializerFactory.shop(userId, shopDetails, region, shopAddress);

        // when
        shop.increaseBookmarkCount();

        // then
        assertThat(shop.getBookmarkCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("북마크 개수를 감소시킨다.")
    void decreaseBookMarks() {
        // given
        var userId = 1L;
        var shopDetails = DataInitializerFactory.shopDetails();
        var shopAddress = DataInitializerFactory.shopAddress();
        var region = DataInitializerFactory.region();
        var shop = DataInitializerFactory.shop(userId, shopDetails, region, shopAddress);

        // when
        shop.increaseBookmarkCount();
        shop.decreaseBookmarkCount();

        // then
        assertThat(shop.getBookmarkCount()).isZero();
    }

    @Test
    @DisplayName("북마크 개수가 0일때 감소시키면 예외가 발생한다")
    void bookMarkThrowsExceptionWhenBookMarkCountisZero() {
        // given
        var userId = 1L;
        var shopDetails = DataInitializerFactory.shopDetails();
        var shopAddress = DataInitializerFactory.shopAddress();
        var region = DataInitializerFactory.region();
        var shop = DataInitializerFactory.shop(userId, shopDetails, region, shopAddress);

        // when & then
        assertThatThrownBy(shop::decreaseBookmarkCount)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("북마크 개수가 0 일 때 북마크 개수를 줄이는 것은 불가능합니다.");
    }
}